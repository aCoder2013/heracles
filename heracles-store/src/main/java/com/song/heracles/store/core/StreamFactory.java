package com.song.heracles.store.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author song
 */
public interface StreamFactory {

	Stream getStream(String name);

	Stream getOrOpenStream(String name);

	CompletableFuture<Void> asyncCreateStream(String name);

	CompletableFuture<List<Void>> closeStreams();
}
