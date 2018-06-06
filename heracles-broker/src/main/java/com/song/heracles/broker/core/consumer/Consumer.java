package com.song.heracles.broker.core.consumer;

import com.song.heracles.broker.core.Message;
import com.song.heracles.broker.core.TopicPartition;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author song
 */
public interface Consumer {

	CompletableFuture<Void> start();

	String getTopic();

	TopicPartition getTopicPartition();

	CompletableFuture<List<Message>> pullMessages(int maxNumber);

	CompletableFuture<Void> close();

}
