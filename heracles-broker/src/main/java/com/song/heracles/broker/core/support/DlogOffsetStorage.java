package com.song.heracles.broker.core.support;

import com.song.heracles.broker.core.Offset;
import com.song.heracles.broker.core.OffsetStorage;
import com.song.heracles.broker.core.TopicPartition;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author song
 */
@Slf4j
public class DlogOffsetStorage implements OffsetStorage {

	@Override
	public void start() {

	}

	@Override
	public Offset get(TopicPartition topicPartition) {
		return null;
	}

	@Override
	public void update(TopicPartition topicPartition, Offset offset) {

	}

	@Override
	public void persist(TopicPartition topicPartition, Offset offset) {

	}

	@Override
	public void close() throws IOException {

	}
}
