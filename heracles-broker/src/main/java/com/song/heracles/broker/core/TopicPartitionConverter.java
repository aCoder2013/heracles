package com.song.heracles.broker.core;

/**
 * @author song
 */
public interface TopicPartitionConverter {

	TopicPartition convert(String name);

}
