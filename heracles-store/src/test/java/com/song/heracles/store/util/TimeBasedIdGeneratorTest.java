package com.song.heracles.store.util;

import static org.junit.Assert.assertEquals;

import com.song.heracles.common.util.IdGenerator;
import com.song.heracles.common.util.TimeBasedIdGenerator;
import org.junit.Test;

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