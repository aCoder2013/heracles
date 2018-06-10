package com.song.heracles.broker.core;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Define a relationship between virtual topic and actual partitioned topic.
 * <p>For example:<pre>
 * 	 base-message-1
 * </pre> wil producer a PartitionedTopic(base-message,1)
 *
 * </p>
 *
 * @author song
 */
@Data
@AllArgsConstructor
public class PartitionedTopic {

	/**
	 * original topic name
	 */
	private String topic;

	private int index;

}
