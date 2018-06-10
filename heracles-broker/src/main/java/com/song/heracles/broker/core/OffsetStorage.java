package com.song.heracles.broker.core;

import java.io.Closeable;

/**
 * @author song
 */
public interface OffsetStorage extends Closeable {

	void start() throws Exception;

	/**
	 * Get offset from cache, if it's not in the cache ,then it will try to fetch offset from the storage
	 */
	Offset readOffsetFromCache(PartitionedTopic partitionedTopic) throws Exception;

	/**
	 * Get offset directly from storage
	 */
	Offset readOffsetFromStorage(PartitionedTopic partitionedTopic) throws Exception;

	/**
	 * Update offset in the cache
	 */
	void updateOffset(PartitionedTopic partitionedTopic, Offset offset);

	/**
	 * Persist offset into storage
	 */
	void persistOffset(PartitionedTopic partitionedTopic) throws Exception;
}
