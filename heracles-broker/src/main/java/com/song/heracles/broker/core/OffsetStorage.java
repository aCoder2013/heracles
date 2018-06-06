package com.song.heracles.broker.core;

import java.io.Closeable;

/**
 * @author song
 */
public interface OffsetStorage extends Closeable {

	void start();

	Offset get(TopicPartition topicPartition);

	void update(TopicPartition topicPartition, Offset offset);

	void persisit(TopicPartition topicPartition, Offset offset);
}
