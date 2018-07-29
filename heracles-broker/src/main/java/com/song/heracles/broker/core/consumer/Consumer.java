package com.song.heracles.broker.core.consumer;

import com.song.heracles.broker.core.PartitionedTopic;
import com.song.heracles.broker.core.message.Message;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author song
 */
public interface Consumer {

    CompletableFuture<Void> start();

    String getTopic();

    PartitionedTopic getPartitionedTopic();

    CompletableFuture<List<Message>> pullMessages(int maxNumber);

    CompletableFuture<Void> close();

}
