package com.song.heracles.store.util;

import com.song.heracles.common.util.IdGenerator;
import com.song.heracles.common.util.TimeBasedIdGenerator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author song
 */
public class TimeBasedIdGeneratorTest {

	@Test
	public void nextId() {
		long now = System.currentTimeMillis();
		IdGenerator idGenerator = new TimeBasedIdGenerator(now);
		assertEquals(now + 1, idGenerator.nextId());
	}
}