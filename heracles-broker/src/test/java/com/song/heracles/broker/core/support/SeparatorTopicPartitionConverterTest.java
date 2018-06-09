package com.song.heracles.broker.core.support;

import com.song.heracles.broker.core.TopicPartition;
import com.song.heracles.broker.core.TopicPartitionConverter;
import org.junit.Test;

import static org.junit.Assert.*;

public class SeparatorTopicPartitionConverterTest {

    @Test
    public void convert() {
        TopicPartitionConverter topicPartitionConverter = new SeparatorTopicPartitionConverter();
        TopicPartition topicPartition = topicPartitionConverter.convert("base-topic-1");
        assertEquals("base-topic", topicPartition.getTopic());
        assertEquals(1, topicPartition.getIndex());
    }

    @Test
    public void convertNonPartitionTopic() {
        TopicPartitionConverter converter = new SeparatorTopicPartitionConverter();
        TopicPartition topicPartition = converter.convert("base-topic");
        assertEquals("base-topic", topicPartition.getTopic());
        assertEquals(0, topicPartition.getIndex());
    }
}