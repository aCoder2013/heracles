package com.song.heracles.broker.core;

/**
 * @author song
 */
public interface TopicPartitionConverter {

    PartitionedTopic convert(String name);

}
