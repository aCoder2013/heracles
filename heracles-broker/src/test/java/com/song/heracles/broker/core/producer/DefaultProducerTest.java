package com.song.heracles.broker.core.producer;

import com.song.heracles.broker.core.PartitionedTopic;
import com.song.heracles.common.concurrent.OrderedExecutor;
import com.song.heracles.common.exception.HeraclesException;
import com.song.heracles.store.core.StreamFactory;
import com.song.heracles.store.core.support.DefaultStreamFactory;

import org.apache.distributedlog.DLSN;
import org.apache.distributedlog.DistributedLogConfiguration;
import org.apache.distributedlog.DistributedLogConstants;
import org.apache.distributedlog.api.namespace.Namespace;
import org.apache.distributedlog.api.namespace.NamespaceBuilder;
import org.apache.distributedlog.common.concurrent.FutureUtils;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import io.netty.buffer.Unpooled;

/**
 * @author song
 */
public class DefaultProducerTest {

	private Producer producer;

	@Before
	public void setUp() throws Exception {
		DistributedLogConfiguration dlConfig = new DistributedLogConfiguration();
		Namespace namespace = NamespaceBuilder.newBuilder()
			.conf(dlConfig)
			.uri(URI.create("distributedlog://127.0.0.1:7000/messaging/my_namespace"))
			.regionId(DistributedLogConstants.LOCAL_REGION_ID)
			.clientId("test-client-id")
			.build();
		StreamFactory streamFactory = new DefaultStreamFactory("test-cliend", dlConfig, namespace, OrderedExecutor.newBuilder().build());

		PartitionedTopic partitionedTopic = new PartitionedTopic("messaging-stream", 1);
		producer = new DefaultProducer(partitionedTopic, streamFactory.getOrOpenStream(partitionedTopic.getOriginalTopic()));
		FutureUtils.ignore(producer.start());
	}

	@Test
	public void send() throws HeraclesException, InterruptedException {
		DLSN send = producer.send(Unpooled.wrappedBuffer("Hello World".getBytes()));
		System.out.println(send);
	}

	@Test
	public void sendAsync() {

	}
}