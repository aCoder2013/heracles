package com.song.heracles.store.core.support;

import com.song.heracles.common.concurrent.OrderedExecutor;
import com.song.heracles.common.util.IdGenerator;
import com.song.heracles.common.util.TimeBasedIdGenerator;
import com.song.heracles.store.core.Stream;
import com.song.heracles.store.core.StreamFactory;
import com.song.heracles.store.core.StreamOp;
import com.song.heracles.store.exception.HeraclesStorageException;
import com.song.heracles.store.exception.StreamNotAvailableStorageException;
import com.song.heracles.store.exception.StreamNotReadyStorageException;
import lombok.extern.slf4j.Slf4j;
import org.apache.distributedlog.DLSN;
import org.apache.distributedlog.DistributedLogConfiguration;
import org.apache.distributedlog.api.AsyncLogReader;
import org.apache.distributedlog.api.AsyncLogWriter;
import org.apache.distributedlog.api.DistributedLogManager;
import org.apache.distributedlog.api.namespace.Namespace;
import org.apache.distributedlog.common.concurrent.FutureUtils;
import org.apache.distributedlog.exceptions.AlreadyClosedException;
import org.apache.distributedlog.exceptions.OwnershipAcquireFailedException;
import org.apache.distributedlog.exceptions.StreamUnavailableException;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author song
 */
@Slf4j
public class DefaultStream implements Stream {

	private final String streamName;

	private DistributedLogManager distributedLogManager;

	private volatile String owner;
	private volatile StreamState streamState;
	private volatile AsyncLogWriter writer;

	private final IdGenerator idGenerator = new TimeBasedIdGenerator(System.currentTimeMillis());

	private final DistributedLogConfiguration dlConfig;
	private final Namespace dlNamespace;
	private final String clientId;
	private volatile Throwable lastException;
	private final OrderedExecutor orderedExecutor;

	private final ReentrantLock closeLock = new ReentrantLock();

	private final StreamFactory streamFactory;

	public DefaultStream(String streamName, String owner, DistributedLogConfiguration dlConfig, Namespace dlNamespace,
						 String clientId, OrderedExecutor orderedExecutor,
						 StreamFactory streamFactory) {
		this.streamState = StreamState.UNINITIALIZED;
		this.streamName = streamName;
		this.owner = owner;
		this.dlConfig = dlConfig;
		this.dlNamespace = dlNamespace;
		this.clientId = clientId;
		this.orderedExecutor = orderedExecutor;
		this.streamFactory = streamFactory;
	}

	@Override
	public CompletableFuture<Void> start() {
		CompletableFuture<Void> completableFuture = new CompletableFuture<>();
		this.orderedExecutor.chooseThread(this.streamName).submit(() -> {
			this.streamState = StreamState.INITIALIZING;
			try {
				this.distributedLogManager = this.dlNamespace.openLog(this.streamName);
			} catch (IOException e) {
				setStreamInErrorState();
				completableFuture.completeExceptionally(e);
				return;
			}
			acquireStream().thenAccept(asyncLogWriter -> {
				synchronized (DefaultStream.this) {
					this.streamState = StreamState.INITIALIZED;
					this.writer = asyncLogWriter;
					completableFuture.complete(null);
				}
			}).exceptionally(throwable -> {
				log.error("Failed to acquire stream " + streamName, throwable);
				closeLock.tryLock();
				setStreamInErrorState();
				try {
					if (streamState == StreamState.INITIALIZED) {
						FutureUtils.ignore(writer.asyncClose());
						FutureUtils.ignore(distributedLogManager.asyncClose());
					}
					completableFuture.completeExceptionally(throwable);
				} finally {
					closeLock.unlock();
				}
				return null;
			});
		});
		return completableFuture;
	}

	private CompletableFuture<AsyncLogWriter> acquireStream() {
		CompletableFuture<AsyncLogWriter> completableFuture = new CompletableFuture<>();
		this.distributedLogManager.openAsyncLogWriter().whenCompleteAsync((asyncLogWriter, throwable) -> {
			if (throwable != null) {
				processAcquireStreamFailed(completableFuture, throwable);
			} else {
				processAcquireStreamSuccess(completableFuture, asyncLogWriter);
			}
		}, this.orderedExecutor.chooseThread(getStreamName()));
		return completableFuture;
	}

	private void processAcquireStreamSuccess(CompletableFuture<AsyncLogWriter> completableFuture, AsyncLogWriter asyncLogWriter) {
		completableFuture.complete(asyncLogWriter);
	}

	private void processAcquireStreamFailed(CompletableFuture<AsyncLogWriter> completableFuture, Throwable throwable) {
		if (throwable instanceof AlreadyClosedException) {
			log.error("Encountered unexpected exception when writing data into stream : ", this.streamName, throwable);
		} else {
			log.error("Failed to acquire stream :" + this.streamName, throwable);
		}
		setStreamStatus(StreamState.ERROR, StreamState.INITIALIZING, null, throwable);
		completableFuture.completeExceptionally(throwable);
	}

	@Override
	public String getStreamName() {
		return this.streamName;
	}

	@Override
	public String getOwner() {
		return this.owner;
	}

	@Override
	public CompletableFuture<AsyncLogReader> asyncOpenReader(DLSN dlsn) {
		CompletableFuture<AsyncLogReader> completableFuture = new CompletableFuture<>();
		orderedExecutor.chooseThread(streamName).submit(() -> {
			try {
				AsyncLogReader asyncLogReader = distributedLogManager.getAsyncLogReader(dlsn);
				if (asyncLogReader != null) {
					completableFuture.complete(asyncLogReader);
				}else {
					completableFuture.completeExceptionally(new StreamUnavailableException("Get AsyncLogReader from DLog failed."));
				}
			} catch (Exception e) {
				completableFuture.completeExceptionally(e);
			}
		});
		return completableFuture;
	}

	@Override
	public void submitOp(StreamOp streamOp) {
		if (StreamState.isUnavailable(streamState)) {
			streamOp.fail(new StreamNotAvailableStorageException("Stream " + streamName + " is not available."));
		} else if (streamState == StreamState.INITIALIZED && writer != null) {
			executeOperation(streamOp);
		} else {
			synchronized (this) {
				if (StreamState.isUnavailable(streamState)) {
					streamOp.fail(new StreamNotAvailableStorageException("Stream " + streamName + " is not available."));
				} else if (streamState == StreamState.INITIALIZED) {
					executeOperation(streamOp);
				} else {
					streamOp.fail(new StreamNotReadyStorageException("Stream " + streamName + " is not ready yet."));
				}
			}
		}
	}

	@Override
	public CompletableFuture<Void> closeAsync() {
		CompletableFuture<Void> future = new CompletableFuture<>();
		if (streamState == StreamState.CLOSING || streamState == StreamState.CLOSED) {
			log.warn("Stream is closing or already closed :{}.", streamState);
			future.complete(null);
		} else if (streamState == StreamState.ERROR) {
			log.warn("Stream in in error state,no need to close.");
			future.complete(null);
		} else {
			closeLock.tryLock();
			try {
				if (streamState == StreamState.CLOSING || streamState == StreamState.CLOSED) {
					log.warn("Stream is closing or already closed :{}.", streamState);
					future.complete(null);
				} else if (streamState == StreamState.ERROR) {
					log.warn("Stream in in error state,no need to close.");
					future.complete(null);
				} else {
					streamState = StreamState.CLOSING;
					if (writer != null) {
						writer.asyncClose().thenRun(() -> log.info("Writer close successfully."))
							.exceptionally(throwable -> {
								log.info("Failed to close writer", throwable);
								return null;
							});
					}
					future.complete(null);
				}
			} finally {
				closeLock.unlock();
			}
		}
		return future;
	}

	private synchronized AsyncLogWriter setStreamStatus(StreamState newState,
														StreamState oldState,
														AsyncLogWriter writer,
														Throwable t) {
		if (oldState != this.streamState) {
			log.info("Stream {} status already changed from {} -> {} when trying to change it to {}",
				this.streamName, oldState, this.streamState, newState);
			return null;
		}

		String owner = null;
		if (t instanceof OwnershipAcquireFailedException) {
			owner = ((OwnershipAcquireFailedException) t).getCurrentOwner();
		}

		AsyncLogWriter oldWriter = this.writer;
		this.writer = writer;
		if (null != owner && owner.equals(clientId)) {
			log.error("I am waiting myself {} to release lock on stream {}, so have to shut myself down :", owner, this.streamName, t);
			// I lost the ownership but left a lock over zookeeper
			// I should not ask client to redirect to myself again as I can't handle it :(
			// shutdown myself
			this.owner = null;
		} else {
			this.owner = owner;
		}
		this.lastException = t;
		this.streamState = newState;
		if (StreamState.INITIALIZED == newState) {
			log.info("Inserted acquired stream {} -> writer {}", this.streamName, this);
		} else {
			log.info("Removed acquired stream {} -> writer {}", this.streamName, this);
		}
		return oldWriter;
	}

	private void executeOperation(final StreamOp streamOp) {
		final AsyncLogWriter writer;
		final Throwable lastException;
		synchronized (this) {
			writer = this.writer;
			lastException = this.lastException;
		}
		if (writer != null) {
			this.orderedExecutor.chooseThread(this.orderedExecutor).submit(() -> {
				streamOp.execute(writer, idGenerator)
					.exceptionally(throwable -> {
						if (throwable instanceof HeraclesStorageException) {
							HeraclesStorageException heraclesStorageException = (HeraclesStorageException) throwable;
							log.error("Failed to execute streamOp with code :" + heraclesStorageException.getCode(), heraclesStorageException);
							streamOp.fail(throwable);
						} else {
							log.error("Failed to execute streamOp ", throwable);
							streamOp.fail(throwable);
						}
						return null;
					});
			});
		} else {
			if (lastException != null) {
				streamOp.fail(lastException);
			} else {
				streamOp.fail(new StreamNotAvailableStorageException("Stream " + streamName + " is unavailable."));
			}
		}
	}

	private synchronized void setStreamInErrorState() {
		if (StreamState.CLOSING == streamState || StreamState.CLOSED == streamState) {
			return;
		}
		this.streamState = StreamState.ERROR;
	}

	@Override
	public String toString() {
		return "DefaultStream{" +
				"streamName='" + streamName + '\'' +
				", owner='" + owner + '\'' +
				", streamState=" + streamState +
				", clientId='" + clientId + '\'' +
				'}';
	}

	public enum StreamState {
		UNINITIALIZED(-1),
		INITIALIZING(0),
		INITIALIZED(1),
		CLOSING(2),
		CLOSED(3),
		ERROR(4);

		final int code;

		StreamState(int code) {
			this.code = code;
		}

		int getCode() {
			return code;
		}

		public static boolean isUnavailable(StreamState status) {
			return StreamState.ERROR == status || StreamState.CLOSING == status || StreamState.CLOSED == status;
		}
	}
}
