package com.song.heracles.broker.core.producer;

import com.song.heracles.broker.core.PartitionedTopic;
import com.song.heracles.common.exception.HeraclesException;

import org.apache.distributedlog.DLSN;

import java.util.concurrent.CompletableFuture;

import io.netty.buffer.ByteBuf;

/**
 * @author song
 */
public interface Producer {

	String getTopic();

	PartitionedTopic getPartitionedTopic();

	CompletableFuture<Void> start();

	DLSN send(ByteBuf payload) throws InterruptedException, HeraclesException;

	CompletableFuture<DLSN> sendAsync(ByteBuf payload);

	CompletableFuture<Void> close();

}
