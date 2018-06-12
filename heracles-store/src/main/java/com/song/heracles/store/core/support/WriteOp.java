package com.song.heracles.store.core.support;

import com.song.heracles.common.constants.ErrorCode;
import com.song.heracles.common.util.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.distributedlog.LogRecord;
import org.apache.distributedlog.api.AsyncLogWriter;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

/**
 * @author song
 */
@Slf4j
public class WriteOp extends AbstractStreamOp<WriteResponse> {

	private final ByteBuffer payload;

	public WriteOp(String streamName, ByteBuffer payload) {
		super(streamName);
		this.payload = payload;
	}

	@Override
	public CompletableFuture<WriteResponse> doExecute(AsyncLogWriter asyncLogWriter, IdGenerator idGenerator) {
		CompletableFuture<WriteResponse> completableFuture = new CompletableFuture<>();
		if (!streamName.equals(asyncLogWriter.getStreamName())) {
			log.error("Stream name mismatch : {}, writer : {}.", streamName, asyncLogWriter.getStreamName());
			WriteResponse response = new WriteResponse();
			response.setCode(ErrorCode.ILLEGAL_STATE);
			response.setMessage("Stream name mismatch : " + streamName + ",dlog writer name :" + asyncLogWriter.getStreamName());
			completableFuture.complete(response);
			return completableFuture;
		}
		LogRecord logRecord = new LogRecord(idGenerator.nextId(), payload);
		asyncLogWriter.write(logRecord)
			.thenAccept(dlsn -> {
				WriteResponse response = new WriteResponse();
				response.setCode(ErrorCode.OK);
				response.setDlsn(dlsn);
				completableFuture.complete(response);
			})
			.exceptionally(throwable -> {
				completableFuture.completeExceptionally(throwable);
				return null;
			});
		return completableFuture;
	}

	@Override
	public void fail(ErrorCode errorCode, String message) {
		setResponse(new WriteResponse(errorCode, message));
	}
}
