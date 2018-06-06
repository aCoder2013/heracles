package com.song.heracles.broker.core.consumer;

import com.song.heracles.broker.core.Message;
import com.song.heracles.broker.core.Offset;
import com.song.heracles.broker.core.TopicPartition;
import com.song.heracles.store.core.Stream;

import org.apache.distributedlog.LogRecordWithDLSN;
import org.apache.distributedlog.api.AsyncLogReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author song
 */
@Slf4j
public class DefaultConsumer implements Consumer {

	private String topic;

	private TopicPartition topicPartition;

	private final Stream stream;

	private volatile Offset offset = null;

	private volatile AsyncLogReader currentLogReader = null;

	public DefaultConsumer(String topic, TopicPartition topicPartition, Stream stream, Offset offset) {
		this.topic = topic;
		this.topicPartition = topicPartition;
		this.stream = stream;
		checkNotNull(offset);
		this.offset = offset;
	}

	@Override
	public CompletableFuture<Void> start() {
		CompletableFuture<Void> startFuture = new CompletableFuture<>();
		stream.asyncOpenReader(offset.getDlsn()).thenAccept(asyncLogReader -> {
			synchronized (this) {
				currentLogReader = asyncLogReader;
				startFuture.complete(null);
			}
		}).exceptionally(throwable -> {
			startFuture.completeExceptionally(throwable);
			return null;
		});
		return startFuture;
	}

	@Override
	public String getTopic() {
		return this.topic;
	}

	@Override
	public TopicPartition getTopicPartition() {
		return this.topicPartition;
	}

	@Override
	public CompletableFuture<List<Message>> pullMessages(int maxNumber) {
		CompletableFuture<List<Message>> pullMessageFuture = new CompletableFuture<>();
		currentLogReader.readBulk(maxNumber).thenAccept(logRecordWithDLSNS -> {
			if (logRecordWithDLSNS != null) {
				List<Message> messages = new ArrayList<>(logRecordWithDLSNS.size());
				for (LogRecordWithDLSN logRecordWithDLSN : logRecordWithDLSNS) {
					messages.add(new Message(new Offset(logRecordWithDLSN.getDlsn()), logRecordWithDLSN.getPayload()));
				}
				pullMessageFuture.complete(messages);
			} else {
				pullMessageFuture.complete(Collections.emptyList());
			}
		}).exceptionally(throwable -> {
			log.error("Pull message failed :" + topicPartition.toString(), throwable);
			pullMessageFuture.completeExceptionally(throwable);
			return null;
		});
		return pullMessageFuture;
	}

	@Override
	public CompletableFuture<Void> close() {
		CompletableFuture<Void> closeFuture = new CompletableFuture<>();
		currentLogReader.asyncClose()
			.thenAccept(aVoid -> closeFuture.complete(null))
			.exceptionally(throwable -> {
				log.error("Close consumer failed :" + topicPartition.toString(), throwable);
				closeFuture.completeExceptionally(throwable);
				return null;
			});
		return closeFuture;
	}
}
