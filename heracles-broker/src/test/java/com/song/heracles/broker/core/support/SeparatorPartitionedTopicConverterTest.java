package com.song.heracles.broker.core.support;

import com.song.heracles.broker.core.PartitionedTopic;
import com.song.heracles.broker.core.TopicPartitionConverter;
import org.junit.Test;

import static org.junit.Assert.*;

public class SeparatorPartitionedTopicConverterTest {

    @Test
    public void convert() {
        TopicPartitionConverter topicPartitionConverter = new SeparatorTopicPartitionConverter();
        PartitionedTopic partitionedTopic = topicPartitionConverter.convert("base-topic-1");
        assertEquals("base-topic", partitionedTopic.getTopic());
        assertEquals(1, partitionedTopic.getIndex());
    }

    @Test
    public void convertNonPartitionTopic() {
        TopicPartitionConverter converter = new SeparatorTopicPartitionConverter();
        PartitionedTopic partitionedTopic = converter.convert("base-topic");
        assertEquals("base-topic", partitionedTopic.getTopic());
        assertEquals(0, partitionedTopic.getIndex());
    }
}