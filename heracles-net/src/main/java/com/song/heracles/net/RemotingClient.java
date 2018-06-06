package com.song.heracles.net;

import com.google.common.net.HostAndPort;

import com.song.heracles.common.util.MathUtils;
import com.song.heracles.net.proto.HeraclesApiGrpc;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.grpc.ManagedChannel;
import io.vertx.core.Vertx;
import io.vertx.grpc.VertxChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author song
 */
@Slf4j
public class RemotingClient implements Closeable {

	private final Vertx vertx = Vertx.vertx();
	private final List<String> servers;

	private final ConcurrentMap<String, ManagedChannel> channels = new ConcurrentHashMap<>();

	private final AtomicReference<String> serverChoosen = new AtomicReference<>(null);

	private final AtomicInteger serverIndex = new AtomicInteger(0);

	public RemotingClient(List<String> servers) {
		checkArgument(servers != null && servers.size() > 0, "Servers can't be null or empty.");
		this.servers = Collections.unmodifiableList(servers);
	}

	/**
	 * Create a heracles api client
	 */
	public HeraclesApiGrpc.HeraclesApiVertxStub createHeraclesClient() {
		String address = serverChoosen.get();
		if (address != null && channels.containsKey(address)) {
			ManagedChannel managedChannel = channels.get(address);
			return HeraclesApiGrpc.newVertxStub(managedChannel);
		}
		synchronized (this) {
			address = serverChoosen.get();
			if (address != null && channels.containsKey(address)) {
				ManagedChannel managedChannel = channels.get(address);
				return HeraclesApiGrpc.newVertxStub(managedChannel);
			}
			for (String server : servers) {
				int index = MathUtils.safeMod(serverIndex.incrementAndGet(), servers.size());
				String newAddress = servers.get(index);
				serverChoosen.set(newAddress);
				log.info("new name server is chosen. OLD: {} , NEW: {}. namesrvIndex = {}", address, newAddress, serverIndex.get());
				HostAndPort hostAndPort = HostAndPort.fromString(server);
				try {
					ManagedChannel managedChannel = VertxChannelBuilder
						.forAddress(vertx, hostAndPort.getHost(), hostAndPort.getPort())
						.usePlaintext(true)
						.build();
					return HeraclesApiGrpc.newVertxStub(managedChannel);
				} catch (Exception e) {
					log.error("Try to establish connection to " + server + " failed,try next one.", e);
				}
			}
		}
		return null;
	}

	@Override
	public void close() throws IOException {
		channels.forEach((s, managedChannel) -> {
			managedChannel.shutdown();
		});
	}
}
