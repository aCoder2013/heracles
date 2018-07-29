package com.song.heracles.broker.core.support;

import com.song.heracles.broker.core.Offset;
import com.song.heracles.broker.core.OffsetStorage;
import com.song.heracles.broker.core.PartitionedTopic;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author song
 */
@Slf4j
public class DlogOffsetStorage implements OffsetStorage {

    @Override
    public void start() throws Exception {

    }

    @Override
    public Offset readOffsetFromCache(PartitionedTopic partitionedTopic) throws Exception {
        return null;
    }

    @Override
    public Offset readOffsetFromStorage(PartitionedTopic partitionedTopic) throws Exception {
        return null;
    }

    @Override
    public void updateOffset(PartitionedTopic partitionedTopic, Offset offset) {

    }

    @Override
    public void persistOffset(PartitionedTopic partitionedTopic) throws Exception {

    }

    @Override
    public void close() throws IOException {

    }
}
