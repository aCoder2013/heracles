package com.song.heracles.broker.core.producer;

import com.song.heracles.broker.core.TopicPartitionConverter;
import com.song.heracles.broker.core.producer.DefaultProducer;
import com.song.heracles.broker.core.producer.Producer;
import com.song.heracles.broker.core.support.SeparatorTopicPartitionConverter;
import com.song.heracles.broker.service.BrokerService;
import com.song.heracles.store.core.Stream;
import com.song.heracles.store.core.StreamFactory;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.apache.distributedlog.common.concurrent.FutureUtils;

/**
 * @author song
 */
public class ProducerManager implements Closeable {

	private final BrokerService brokerService;

	private final TopicPartitionConverter topicPartitionConverter;

	private StreamFactory streamFactory;

	public ProducerManager(BrokerService brokerService) {
		this.brokerService = brokerService;
		this.streamFactory = brokerService.getStreamFactory();
		this.topicPartitionConverter = new SeparatorTopicPartitionConverter();
	}

	public void start() throws IOException {
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
		Producer producer = new DefaultProducer(topicPartitionConverter.convert(topic), stream);
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
