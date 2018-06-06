package com.song.heracles.client.api;

import com.song.heracles.client.configuration.ClientConfiguration;
import com.song.heracles.client.configuration.ProducerConfiguration;
import com.song.heracles.client.exception.HeraclesClientException;
import com.song.heracles.common.util.IdGenerator;
import com.song.heracles.common.util.TimeBasedIdGenerator;
import com.song.heracles.net.RemotingClient;
import com.song.heracles.net.proto.HeraclesApiGrpc;

import org.apache.commons.collections4.CollectionUtils;

import java.io.Closeable;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author song
 */
@Slf4j
public class HeraclesClient implements Closeable {

	private RemotingClient remotingClient;

	private final ClientConfiguration clientConfiguration;

	private final IdGenerator producerIdGenerator = new TimeBasedIdGenerator(System.currentTimeMillis());

	public HeraclesClient(ClientConfiguration clientConfiguration) throws HeraclesClientException {
		this.clientConfiguration = clientConfiguration;
		if (CollectionUtils.isEmpty(clientConfiguration.getServers())) {
			throw new HeraclesClientException("Heracles servers can't be null or empty.");
		}
		remotingClient = new RemotingClient(clientConfiguration.getServers());
	}

	public Producer createProducer(ProducerConfiguration configuration) throws HeraclesClientException {
		HeraclesApiGrpc.HeraclesApiVertxStub heraclesClient = remotingClient.createHeraclesClient();
		if (heraclesClient == null) {
			throw new HeraclesClientException("Create producer failed,may all servers are crashed?");
		}
		return new DefaultProducer(configuration, heraclesClient, producerIdGenerator.nextId());
	}

	public ClientConfiguration getClientConfiguration() {
		return clientConfiguration;
	}

	@Override
	public void close() throws IOException {
		remotingClient.close();
	}
}
