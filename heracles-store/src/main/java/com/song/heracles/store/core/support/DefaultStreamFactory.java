package com.song.heracles.store.core.support;

import com.song.heracles.common.concurrent.OrderedExecutor;
import com.song.heracles.store.core.Stream;
import com.song.heracles.store.core.StreamFactory;

import org.apache.distributedlog.DistributedLogConfiguration;
import org.apache.distributedlog.api.namespace.Namespace;
import org.apache.distributedlog.common.concurrent.FutureUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.extern.slf4j.Slf4j;

/**
 * @author song
 */
@Slf4j
public class DefaultStreamFactory implements StreamFactory {

	private final ConcurrentMap<String/*Stream Name*/, Stream> streamCache = new ConcurrentHashMap<>();

	private final String clientId;

	private final DistributedLogConfiguration dlConfig;

	private final Namespace namespace;

	private final OrderedExecutor orderedExecutor;

	public DefaultStreamFactory(String clientId, DistributedLogConfiguration dlConfig, Namespace namespace, OrderedExecutor orderedExecutor) {
		this.clientId = clientId;
		this.dlConfig = dlConfig;
		this.namespace = namespace;
		this.orderedExecutor = orderedExecutor;
	}

	@Override
	public Stream getStream(String name) {
		return streamCache.get(name);
	}

	@Override
	public Stream getOrOpenStream(String name) {
		Stream stream = streamCache.get(name);
		if (stream != null) {
			return stream;
		}
		synchronized (this) {
			stream = new DefaultStream(name, null, dlConfig, namespace, clientId, orderedExecutor, this);
			Stream oldStream = streamCache.putIfAbsent(name, stream);
			if (oldStream != null) {
				stream = oldStream;
			} else {
				log.info("Create stream [{}] .", name);
				stream.start();
			}
		}
		return stream;
	}

	@Override
	public CompletableFuture<Void> asyncCreateStream(String name) {
		CompletableFuture<Void> completableFuture = new CompletableFuture<>();
		orderedExecutor.chooseThread(name).submit(() -> {
			try {
				namespace.createLog(name);
				completableFuture.complete(null);
			} catch (IOException e) {
				completableFuture.completeExceptionally(e);
			}
		});
		return completableFuture;
	}

	@Override
	public CompletableFuture<List<Void>> closeStreams() {
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		log.info("Closing all streams :" + streamCache.size());
		streamCache.values().forEach(stream -> futures.add(stream.closeAsync()));
		return FutureUtils.collect(futures);
	}

}
