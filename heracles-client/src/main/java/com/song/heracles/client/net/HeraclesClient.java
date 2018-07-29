package com.song.heracles.client.net;

import com.song.heracles.client.api.Consumer;
import com.song.heracles.client.api.DefaultConsumer;
import com.song.heracles.client.api.DefaultProducer;
import com.song.heracles.client.api.Producer;
import com.song.heracles.client.configuration.ClientConfiguration;
import com.song.heracles.client.configuration.ConsumerConfiguration;
import com.song.heracles.client.configuration.ProducerConfiguration;
import com.song.heracles.client.exception.HeraclesClientException;
import com.song.heracles.common.util.IdGenerator;
import com.song.heracles.common.util.TimeBasedIdGenerator;
import com.song.heracles.net.RemotingClient;
import com.song.heracles.net.proto.HeraclesApiGrpc;
import com.song.heracles.net.proto.HeraclesApiGrpc.HeraclesApiVertxStub;
import java.io.Closeable;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @author song
 */
@Slf4j
public class HeraclesClient implements Closeable {

    private RemotingClient remotingClient;

    private final ClientConfiguration clientConfiguration;

    private final IdGenerator producerIdGenerator = new TimeBasedIdGenerator(
        System.currentTimeMillis());

    public HeraclesClient(ClientConfiguration clientConfiguration) throws HeraclesClientException {
        this.clientConfiguration = clientConfiguration;
        if (CollectionUtils.isEmpty(clientConfiguration.getServers())) {
            throw new HeraclesClientException("Heracles servers can't be null or empty.");
        }
        remotingClient = new RemotingClient(clientConfiguration.getServers());
    }

    public Producer createProducer(ProducerConfiguration configuration)
        throws HeraclesClientException {
        HeraclesApiGrpc.HeraclesApiVertxStub heraclesClient = remotingClient.createHeraclesClient();
        if (heraclesClient == null) {
            throw new HeraclesClientException(
                "Create producer failed,may all servers are crashed?");
        }
        return new DefaultProducer(configuration, heraclesClient, producerIdGenerator.nextId());
    }

    public Consumer createConsumer(ConsumerConfiguration consumerConfiguration)
        throws HeraclesClientException {
        HeraclesApiVertxStub heraclesClient = remotingClient.createHeraclesClient();
        if (heraclesClient == null) {
            throw new HeraclesClientException(
                "Create consumer failed,may all servers are crashed?");
        }
        return new DefaultConsumer(consumerConfiguration, heraclesClient,
            System.currentTimeMillis());
    }

    public ClientConfiguration getClientConfiguration() {
        return clientConfiguration;
    }

    @Override
    public void close() throws IOException {
        remotingClient.close();
    }
}
