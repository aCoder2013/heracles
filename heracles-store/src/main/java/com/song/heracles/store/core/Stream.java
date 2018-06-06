package com.song.heracles.store.core;

import org.apache.distributedlog.DLSN;
import org.apache.distributedlog.api.AsyncLogReader;

import java.util.concurrent.CompletableFuture;

/**
 * @author song
 */
public interface Stream {

	CompletableFuture<Void> start();

	String getStreamName();

	String getOwner();

	CompletableFuture<AsyncLogReader> asyncOpenReader(DLSN dlsn);

	void submitOp(StreamOp streamOp);

	CompletableFuture<Void> closeAsync();
}
