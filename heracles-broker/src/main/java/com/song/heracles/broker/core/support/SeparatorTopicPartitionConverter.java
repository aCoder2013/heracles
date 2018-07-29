package com.song.heracles.broker.core.support;

import com.song.heracles.broker.core.PartitionedTopic;
import com.song.heracles.broker.core.TopicPartitionConverter;
import org.apache.commons.lang.StringUtils;

/**
 * Convert a given topic name to {@link PartitionedTopic} based its separator.
 * <p>For example, this expression <pre> {@code
 * 	base-stream-1
 * }</pre>
 * will produce a {@link PartitionedTopic} whose name is base-stream && index is 1.
 *
 * </p>
 *
 * @author song
 */
public class SeparatorTopicPartitionConverter implements TopicPartitionConverter {

    private static final String DEFAULT_SEPARATOR = "-";

    private final String separator;

    public SeparatorTopicPartitionConverter() {
        this(DEFAULT_SEPARATOR);
    }

    public SeparatorTopicPartitionConverter(String separator) {
        this.separator = separator;
    }

    @Override
    public PartitionedTopic convert(String topic) {
        if (StringUtils.isNotBlank(topic)) {
            int separatorIndex = topic.lastIndexOf(separator);
            if (separatorIndex != -1) {
                String name = topic.substring(0, separatorIndex);
                try {
                    int index = Integer.parseInt(topic.substring(separatorIndex + 1));
                    return new PartitionedTopic(name, index);
                } catch (NumberFormatException ignore) {
                }
            }
        }
        return new PartitionedTopic(topic, 0);
    }
}
