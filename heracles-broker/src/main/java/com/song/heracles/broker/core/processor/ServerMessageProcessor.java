package com.song.heracles.broker.core.processor;

import com.google.protobuf.ByteString;

import com.song.heracles.broker.core.ProducerManager;
import com.song.heracles.broker.core.producer.Producer;
import com.song.heracles.broker.service.BrokerService;
import com.song.heracles.common.constants.ErrorCode;
import com.song.heracles.common.exception.HeraclesException;
import com.song.heracles.common.util.Result;
import com.song.heracles.common.util.ValidateUtils;
import com.song.heracles.net.proto.HeraclesApiGrpc;
import com.song.heracles.net.proto.HeraclesProto;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.netty.buffer.Unpooled;
import io.vertx.core.Future;
import lombok.extern.slf4j.Slf4j;

/**
 * @author song
 */
@Slf4j
public class ServerMessageProcessor extends HeraclesApiGrpc.HeraclesApiVertxImplBase {

	private final BrokerService brokerService;

	private final ConcurrentMap<Long, CompletableFuture<Producer>> producers = new ConcurrentHashMap<>();

	public ServerMessageProcessor(BrokerService brokerService) {
		this.brokerService = brokerService;
	}

	@Override
	public void handleProducerConnect(HeraclesProto.ProducerConnectRequest request, Future<HeraclesProto.ProducerResponse> response) {
		long producerId = request.getProducerId();
		String topic = request.getTopic();
		String producerName = request.getProducerName();
		if (StringUtils.isBlank(producerName)) {
			producerName = brokerService.getProducerNameGenerator().getNextId();
		}
		Result result = ValidateUtils.checkTopicName(topic);
		if (!result.isSuccess()) {
			response.fail(result.getMessage());
			return;
		}
		CompletableFuture<Producer> producerFuture = new CompletableFuture<>();
		CompletableFuture<Producer> existingProducerFuture = producers.putIfAbsent(producerId, producerFuture);
		if (existingProducerFuture != null) {
			if (existingProducerFuture.isDone() && !existingProducerFuture.isCompletedExceptionally()) {
				Producer producer = existingProducerFuture.getNow(null);
				log.info("Producer with the same producerId is already created: {}", producer);
				HeraclesProto.ProducerResponse producerResponse = HeraclesProto.ProducerResponse.newBuilder()
					.setProducerId(producerId)
					.setProducerName(producerName)
					.build();
				response.complete(producerResponse);
			} else {
				log.warn("Producer[{}-{}] is still creating.", topic, producerId);
				response.fail(new HeraclesException("Producer is creating,ignore repeated request.", ErrorCode.RESOURCE_NOT_READY.getCode()));
				producers.remove(producerId);
			}
		} else {
			ProducerManager producerManager = brokerService.getProducerManager();
			String finalProducerName = producerName;
			producerManager.create(topic)
				.thenAccept(producer -> {
					HeraclesProto.ProducerResponse producerResponse = HeraclesProto.ProducerResponse.newBuilder()
						.setProducerName(finalProducerName)
						.setProducerId(producerId)
						.build();
					log.info("Created producer :{}.", producer);
					response.complete(producerResponse);
					producerFuture.complete(producer);
				})
				.exceptionally(throwable -> {
					log.error("Failed to create producer :" + topic, throwable);
					response.fail(throwable);
					producerFuture.completeExceptionally(throwable);
					producers.remove(producerId);
					return null;
				});
		}
	}

	@Override
	public void handleSendMessage(HeraclesProto.SendMessageRequest request, Future<HeraclesProto.SendMessageResponse> response) {
		HeraclesProto.Message message = request.getMessage();
		ByteString body = message.getBody();
		long producerId = request.getProducerId();
		CompletableFuture<Producer> producerFuture = producers.get(producerId);
		if (producerFuture == null || !producerFuture.isDone() || producerFuture.isCompletedExceptionally()) {
			log.warn("Producer is already closed :[{}]", producerId);
			response.fail(String.format("Producer is already closed :%d", producerId));
			return;
		}
		Producer producer = producerFuture.getNow(null);
		producer.sendAsync(Unpooled.wrappedBuffer(body.toByteArray())).thenAccept(dlsn -> {
			HeraclesProto.SendMessageResponse sendMessageResponse = HeraclesProto.SendMessageResponse.newBuilder()
				.setProducerId(producerId)
				.setMessageId(HeraclesProto.MessageIdData.newBuilder()
					.setLogSegmentSequenceNo(dlsn.getLogSegmentSequenceNo())
					.setEntryId(dlsn.getEntryId())
					.setSlotId(dlsn.getSlotId())
					.build())
				.build();
			response.complete(sendMessageResponse);
		}).exceptionally(throwable -> {
			response.fail(throwable);
			return null;
		});
	}

}
