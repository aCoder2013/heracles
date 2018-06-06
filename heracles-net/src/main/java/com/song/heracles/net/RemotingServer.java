package com.song.heracles.net;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.vertx.grpc.VertxServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author song
 */
@Slf4j
public class RemotingServer {

	private final VertxServer vertxServer;

	private final AtomicReference<State> state = new AtomicReference<>(State.UNINITIALIZED);

	public RemotingServer(VertxServer vertxServer) {
		this.vertxServer = vertxServer;
	}

	public void start() throws IOException {
		if (state.compareAndSet(State.UNINITIALIZED, State.INITIALIZING)) {
			try {
				vertxServer.start();
				state.set(State.INITIALIZED);
			} catch (IOException e) {
				state.set(State.ERROR);
				throw e;
			}
		} else {
			log.warn("Remoting server is initializing or already initialized.");
		}
	}

	public synchronized void shutdown() {
		if (state.get() == State.ERROR || state.get() == State.CLOSING || state.get() == State.CLOSED) {
			log.info("Remoting server is already closed : {}.", state.get());
			return;
		}
		state.set(State.CLOSING);
		vertxServer.shutdown();
		state.set(State.CLOSED);
	}

	public enum State {
		UNINITIALIZED,
		INITIALIZING,
		INITIALIZED,
		CLOSING,
		CLOSED,
		ERROR
	}
}
