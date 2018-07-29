package com.song.heracles.common.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * This is a thread safe time based id generator.
 *
 * @author song
 */
public class TimeBasedIdGenerator implements IdGenerator {

    private AtomicLong idGenerator;

    public TimeBasedIdGenerator(long lastId) {
        idGenerator = new AtomicLong(Math.max(lastId, System.currentTimeMillis()));
    }

    @Override
    public long nextId() {
        return idGenerator.incrementAndGet();
    }
}
