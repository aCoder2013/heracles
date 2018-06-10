package com.song.heracles.broker.core.producer;

import com.song.heracles.broker.core.PartitionedTopic;
import com.song.heracles.common.constants.ErrorCode;
import com.song.heracles.common.exception.HeraclesException;
import com.song.heracles.common.util.Result;
import com.song.heracles.store.core.Stream;
import com.song.heracles.store.core.support.WriteOp;

import org.apache.distributedlog.DLSN;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import io.netty.buffer.ByteBuf;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author song
 */
@Slf4j
@ToString
public class DefaultProducer implements Producer {

	private final PartitionedTopic partitionedTopic;

	private final Stream stream;

	public DefaultProducer(PartitionedTopic partitionedTopic, Stream stream) {
		this.partitionedTopic = partitionedTopic;
		this.stream = stream;
	}

	@Override
	public String getTopic() {
		return this.partitionedTopic.getTopic();
	}

	@Override
	public PartitionedTopic getPartitionedTopic() {
		return this.partitionedTopic;
	}

	@Override
	public CompletableFuture<Void> start() {
		CompletableFuture<Void> completableFuture = new CompletableFuture<>();
		completableFuture.complete(null);
		return completableFuture;
	}

	@Override
	public DLSN send(ByteBuf payload) throws InterruptedException, HeraclesException {
		Result<DLSN> result = new Result<>();
		CountDownLatch latch = new CountDownLatch(1);
		sendAsync(payload)
			.thenAccept(dlsn -> {
				result.setData(dlsn);
				latch.countDown();
			})
			.exceptionally(throwable -> {
				result.setThrowable(throwable);
				latch.countDown();
				return null;
			});
		latch.await();
		if (result.getThrowable() != null) {
			throw new HeraclesException(result.getThrowable());
		}
		return result.getData();
	}

	@Override
	public CompletableFuture<DLSN> sendAsync(ByteBuf payload) {
		CompletableFuture<DLSN> completableFuture = new CompletableFuture<>();
		WriteOp writeOp = new WriteOp(this.partitionedTopic.getOriginalTopic(), payload.nioBuffer());
		stream.submitOp(writeOp);
		writeOp.getResultAsync().thenAccept(writeResponse -> {
			ErrorCode errorCode = writeResponse.getCode();
			if (errorCode == ErrorCode.OK) {
				completableFuture.complete(writeResponse.getDlsn());
			} else {
				completableFuture.completeExceptionally(new HeraclesException(writeResponse.getMessage(), writeResponse.getCode().getCode()));
			}
		}).exceptionally(throwable -> {
			completableFuture.completeExceptionally(throwable);
			return null;
		});
		return completableFuture;
	}

	@Override
	public CompletableFuture<Void> close() {
		return stream.closeAsync();
	}
}
