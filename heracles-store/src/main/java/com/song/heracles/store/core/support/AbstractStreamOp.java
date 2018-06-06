package com.song.heracles.store.core.support;

import com.song.heracles.common.util.IdGenerator;
import com.song.heracles.store.core.StreamOp;
import com.song.heracles.common.constants.ErrorCode;
import com.song.heracles.store.exception.HeraclesStorageException;

import org.apache.distributedlog.api.AsyncLogWriter;
import org.apache.distributedlog.exceptions.OwnershipAcquireFailedException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author song
 */
@Slf4j
public abstract class AbstractStreamOp<Response extends StreamResponse> implements StreamOp {

	String streamName;

	private CompletableFuture<Response> streamOpCompletableFuture = new CompletableFuture<>();

	public AbstractStreamOp(String streamName) {
		this.streamName = streamName;
	}

	@Override
	public String streamName() {
		return this.streamName;
	}

	@Override
	public CompletableFuture<Void> execute(AsyncLogWriter asyncLogWriter, IdGenerator idGenerator) {
		return doExecute(asyncLogWriter, idGenerator)
			.thenAccept(this::setResponse)
			.exceptionally(throwable -> {
				fail(throwable);
				return null;
			});
	}

	@Override
	public void fail(Throwable throwable) {
		log.info("Execute op failed", throwable);
		if (throwable instanceof OwnershipAcquireFailedException) {
			OwnershipAcquireFailedException ownershipAcquireFailedException = (OwnershipAcquireFailedException) throwable;
			fail(ErrorCode.OWNER_ALREADY_EXIST, "There is already a owner exist :" + ownershipAcquireFailedException.getCurrentOwner()
				+ " ,detail:," + ownershipAcquireFailedException.getMessage());
		} else if (throwable instanceof HeraclesStorageException) {
			HeraclesStorageException heraclesStorageException = (HeraclesStorageException) throwable;
			fail(ErrorCode.getByCode(heraclesStorageException.getCode()), heraclesStorageException.getMessage());
		} else if (throwable instanceof CompletionException) {
			CompletionException completionException = (CompletionException) throwable;
			fail(completionException.getCause());
		} else {
			fail(ErrorCode.UNKNOWN_ERROR, "Unknown error :" + throwable.getMessage());
		}
	}

	public abstract CompletableFuture<Response> doExecute(AsyncLogWriter asyncLogWriter, IdGenerator idGenerator);

	void setResponse(Response response) {
		if (streamOpCompletableFuture.isDone()) {
			log.error("Set response multiple times : {}.", response);
			return;
		}
		if (streamOpCompletableFuture.isCancelled()) {
			log.error("Response is already cancelled : {}.", response);
			return;
		}
		streamOpCompletableFuture.complete(response);
	}

	public CompletableFuture<Response> getResultAsync() {
		return streamOpCompletableFuture;
	}

	public abstract void fail(ErrorCode errorCode, String message);
}
