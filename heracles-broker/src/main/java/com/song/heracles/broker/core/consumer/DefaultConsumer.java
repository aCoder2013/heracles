package com.song.heracles.broker.core.consumer;

import com.song.heracles.broker.core.Message;
import com.song.heracles.broker.core.Offset;
import com.song.heracles.broker.core.OffsetStorage;
import com.song.heracles.broker.core.PartitionedTopic;
import com.song.heracles.store.core.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.distributedlog.LogRecordWithDLSN;
import org.apache.distributedlog.api.AsyncLogReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author song
 */
@Slf4j
public class DefaultConsumer implements Consumer {

	private final PartitionedTopic partitionedTopic;

	private final Stream stream;

	private volatile Offset offset;

	private volatile AsyncLogReader currentLogReader = null;

	public DefaultConsumer(PartitionedTopic partitionedTopic, Stream stream, OffsetStorage offsetStorage) throws Exception {
		this.partitionedTopic = partitionedTopic;
		this.stream = stream;
		this.offset = offsetStorage.readOffsetFromCache(partitionedTopic);
	}

	@Override
	public CompletableFuture<Void> start() {
		CompletableFuture<Void> startFuture = new CompletableFuture<>();
		stream.start().thenRun(() -> stream.asyncOpenReader(offset.getDlsn()).thenAccept(asyncLogReader -> {
			currentLogReader = asyncLogReader;
			startFuture.complete(null);
			log.info("Consumer[{}] has started.", partitionedTopic.getOriginalTopic());
		}).exceptionally(throwable -> {
			startFuture.completeExceptionally(throwable);
			return null;
		})).exceptionally(throwable -> {
			startFuture.completeExceptionally(throwable);
			return null;
		});
		return startFuture;
	}

	@Override
	public String getTopic() {
		return this.partitionedTopic.getTopic();
	}

	@Override
	public PartitionedTopic getPartitionedTopic() {
		return this.partitionedTopic;
	}

	@Override
	public CompletableFuture<List<Message>> pullMessages(int maxNumber) {
		//TODO:fix read maxMaxNumber messages
		CompletableFuture<List<Message>> pullMessageFuture = new CompletableFuture<>();
		currentLogReader.readBulk(maxNumber).thenAccept(logRecordWithDLSNS -> {
			if (logRecordWithDLSNS != null && logRecordWithDLSNS.size() > 0) {
				List<Message> messages = new ArrayList<>(logRecordWithDLSNS.size());
				for (LogRecordWithDLSN logRecordWithDLSN : logRecordWithDLSNS) {
					messages.add(new Message(new Offset(logRecordWithDLSN.getDlsn()), logRecordWithDLSN.getPayload()));
				}
				pullMessageFuture.complete(messages);
			} else {
				pullMessageFuture.complete(Collections.emptyList());
			}
		}).exceptionally(throwable -> {
			log.error("Pull message failed :" + partitionedTopic.toString(), throwable);
			pullMessageFuture.completeExceptionally(throwable);
			return null;
		});
		return pullMessageFuture;
	}

	@Override
	public CompletableFuture<Void> close() {
		CompletableFuture<Void> closeFuture = new CompletableFuture<>();
		currentLogReader.asyncClose()
			.thenRun(() -> closeFuture.complete(null))
			.exceptionally(throwable -> {
				log.error("Close consumer failed :" + partitionedTopic.toString(), throwable);
				closeFuture.completeExceptionally(throwable);
				return null;
			});
		return closeFuture;
	}
}
