package com.song.heracles.broker.core;

import com.song.heracles.broker.config.BrokerConfiguration;
import com.song.heracles.broker.core.producer.Producer;
import com.song.heracles.broker.core.producer.DefaultProducer;
import com.song.heracles.broker.core.support.SeparatorTopicPartitionConverter;
import com.song.heracles.broker.service.BrokerService;
import com.song.heracles.store.core.Stream;
import com.song.heracles.store.core.StreamFactory;
import com.song.heracles.store.core.support.DefaultStreamFactory;

import org.apache.distributedlog.DistributedLogConfiguration;
import org.apache.distributedlog.DistributedLogConstants;
import org.apache.distributedlog.api.namespace.Namespace;
import org.apache.distributedlog.api.namespace.NamespaceBuilder;
import org.apache.distributedlog.common.concurrent.FutureUtils;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

/**
 * @author song
 */
public class ProducerManager implements Closeable {

	private final BrokerService brokerService;

	private final TopicPartitionConverter topicPartitionConverter;

	private StreamFactory streamFactory;

	public ProducerManager(BrokerService brokerService) {
		this.brokerService = brokerService;
		this.topicPartitionConverter = new SeparatorTopicPartitionConverter();
	}

	public void start() throws IOException {
		BrokerConfiguration brokerConfiguration = brokerService.getBrokerConfiguration();
		DistributedLogConfiguration dlConfig = brokerService.getdLogConfig();
		Namespace namespace = NamespaceBuilder.newBuilder()
			.conf(dlConfig)
			.uri(URI.create(brokerConfiguration.getDistributedLogUri()))
			.regionId(DistributedLogConstants.LOCAL_REGION_ID)
			.clientId(brokerConfiguration.getClientId())
			.build();
		streamFactory = new DefaultStreamFactory(brokerConfiguration.getClientId(), dlConfig, namespace, brokerService.getOrderedExecutor());
	}

	/**
	 * Create a producer with given topic name.
	 *
	 * @param topic topic of the producer with partition number,
	 *              eg : base-message-1
	 * @return the future
	 */
	public CompletableFuture<Producer> create(String topic) {
		CompletableFuture<Producer> completableFuture = new CompletableFuture<>();
		Stream stream = streamFactory.getOrOpenStream(topic);
		Producer producer = new DefaultProducer(topic, topicPartitionConverter.convert(topic), stream);
		producer.start()
			.thenRun(() -> completableFuture.complete(producer))
			.exceptionally(throwable -> {
				completableFuture.completeExceptionally(throwable);
				return null;
			});
		return completableFuture;
	}

	@Override
	public void close() throws IOException {
		FutureUtils.ignore(streamFactory.closeStreams());
	}
}
