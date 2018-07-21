package com.song.heracles.client.api;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.protobuf.ByteString;
import com.song.heracles.client.configuration.ProducerConfiguration;
import com.song.heracles.client.exception.HeraclesClientException;
import com.song.heracles.common.util.Result;
import com.song.heracles.common.util.ValidateUtils;
import com.song.heracles.net.proto.HeraclesApiGrpc;
import com.song.heracles.net.proto.HeraclesProto;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author song
 */
@Slf4j
public class DefaultProducer implements Producer {

	private final ProducerConfiguration producerConfiguration;

	private String topic;

	private String producerName;

	private final long producerId;

	private long startTimeout = 6000;

	private TimeUnit startTimeoutUnit = TimeUnit.MILLISECONDS;

	private final HeraclesApiGrpc.HeraclesApiVertxStub heraclesClient;

	public DefaultProducer(ProducerConfiguration producerConfiguration, HeraclesApiGrpc.HeraclesApiVertxStub heraclesClient, long producerId) {
		this.producerConfiguration = producerConfiguration;
		Result result = ValidateUtils.checkTopicName(producerConfiguration.getTopic());
		checkArgument(result.isSuccess(), result.getMessage());
		this.topic = producerConfiguration.getTopic();
		this.producerName = producerConfiguration.getProducerName();
		this.startTimeout = producerConfiguration.getStartTimeout();
		this.startTimeoutUnit = producerConfiguration.getStartTimeoutUnit();
		this.heraclesClient = heraclesClient;
		this.producerId = producerId;
	}

	@Override
	public void start() throws InterruptedException, HeraclesClientException {
		Result<Void> result = new Result<>();
		CountDownLatch latch = new CountDownLatch(1);
		HeraclesProto.ProducerConnectRequest producerConnectRequest = HeraclesProto.ProducerConnectRequest.newBuilder()
			.setProducerId(producerId)
			.setTopic(this.topic)
			.setProducerName(StringUtils.defaultString(producerName, ""))
			.build();
		heraclesClient.handleProducerConnect(producerConnectRequest, asyncResult -> {
			if (asyncResult.succeeded()) {
				log.info("Producer started successfully.");
			} else {
				result.setThrowable(asyncResult.cause());
			}
			latch.countDown();
		});
		latch.await(startTimeout, startTimeoutUnit);
		if (result.getThrowable() != null) {
			throw new HeraclesClientException(result.getThrowable());
		}
	}

	@Override
	public String getTopic() {
		return this.topic;
	}

	@Override
	public String producerName() {
		return producerName;
	}

	@Override
	public MessageId send(byte[] message) throws InterruptedException, HeraclesClientException {
		Result<MessageId> result = new Result<>();
		CountDownLatch latch = new CountDownLatch(1);
		sendAsync(message).thenAccept(messageId -> {
			result.setData(messageId);
			latch.countDown();
		}).exceptionally(throwable -> {
			result.setThrowable(throwable);
			latch.countDown();
			return null;
		});
		latch.await();
		if (result.getThrowable() != null) {
			throw new HeraclesClientException(result.getThrowable());
		}
		return result.getData();
	}

	@Override
	public CompletableFuture<MessageId> sendAsync(byte[] message) {
		CompletableFuture<MessageId> sendAsyncFuture = new CompletableFuture<>();
		HeraclesProto.SendMessageRequest sendMessageRequest = HeraclesProto
			.SendMessageRequest
			.newBuilder()
			.setProducerId(producerId)
			.setMessage(HeraclesProto.Message.newBuilder().setBody(ByteString.copyFrom(message)).build())
			.build();
		heraclesClient.handleSendMessage(sendMessageRequest, asyncResult -> {
			if (asyncResult.succeeded()) {
				HeraclesProto.SendMessageResponse sendMessageResponse = asyncResult.result();
				HeraclesProto.MessageIdData messageId = sendMessageResponse.getMessageId();
				sendAsyncFuture.complete(new MessageId(messageId.getLogSegmentSequenceNo(), messageId.getEntryId(), messageId.getSlotId()));
			} else {
				sendAsyncFuture.completeExceptionally(asyncResult.cause());
			}
		});
		return sendAsyncFuture;
	}

	@Override
	public void close() throws IOException {
	}

	public ProducerConfiguration getProducerConfiguration() {
		return producerConfiguration;
	}
}
