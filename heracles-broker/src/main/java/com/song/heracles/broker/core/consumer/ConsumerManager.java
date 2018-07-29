package com.song.heracles.broker.core.consumer;

import com.song.heracles.broker.core.OffsetStorage;
import com.song.heracles.broker.core.PartitionedTopic;
import com.song.heracles.broker.core.TopicPartitionConverter;
import com.song.heracles.broker.core.support.SeparatorTopicPartitionConverter;
import com.song.heracles.broker.service.BrokerService;
import com.song.heracles.store.core.Stream;
import com.song.heracles.store.core.StreamFactory;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsumerManager implements Closeable {

    private final BrokerService brokerService;

    private final StreamFactory streamFactory;

    private final OffsetStorage offsetStorage;

    private final TopicPartitionConverter topicPartitionConverter;

    public ConsumerManager(BrokerService brokerService) {
        this.brokerService = brokerService;
        this.streamFactory = brokerService.getStreamFactory();
        this.offsetStorage = brokerService.getOffsetStorage();
        topicPartitionConverter = new SeparatorTopicPartitionConverter();
    }

    public void start() {

    }

    public CompletableFuture<Consumer> create(String topic) {
        CompletableFuture<Consumer> completableFuture = new CompletableFuture<>();
        this.brokerService.getOrderedExecutor().chooseThread(topic)
            .submit(() -> {
                PartitionedTopic partitionedTopic = topicPartitionConverter.convert(topic);
                Stream stream = streamFactory.getOrOpenStream(topic);
                stream.start().thenRun(() -> {
                    try {
                        Consumer consumer = new DefaultConsumer(partitionedTopic, stream,
                            offsetStorage);
                        consumer.start().thenRun(() -> completableFuture.complete(consumer))
                            .exceptionally(throwable -> {
                                completableFuture.completeExceptionally(throwable);
                                return null;
                            });
                    } catch (Exception e) {
                        completableFuture.completeExceptionally(e);
                    }
                }).exceptionally(throwable -> {
                    completableFuture.completeExceptionally(throwable);
                    return null;
                });
            });
        return completableFuture;
    }

    @Override
    public void close() throws IOException {

    }
}
