package com.song.heracles.broker.core.processor;

import com.google.protobuf.ByteString;
import com.song.heracles.broker.core.Offset;
import com.song.heracles.broker.core.OffsetStorage;
import com.song.heracles.broker.core.consumer.Consumer;
import com.song.heracles.broker.core.consumer.ConsumerManager;
import com.song.heracles.broker.core.producer.Producer;
import com.song.heracles.broker.core.producer.ProducerManager;
import com.song.heracles.broker.service.BrokerService;
import com.song.heracles.common.constants.ErrorCode;
import com.song.heracles.common.exception.HeraclesException;
import com.song.heracles.common.util.Result;
import com.song.heracles.common.util.ValidateUtils;
import com.song.heracles.net.proto.HeraclesApiGrpc;
import com.song.heracles.net.proto.HeraclesProto;
import com.song.heracles.net.proto.HeraclesProto.ConsumerConnectRequest;
import com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse;
import com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageRequest;
import com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse;
import com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse.Builder;
import com.song.heracles.net.proto.HeraclesProto.Message;
import com.song.heracles.net.proto.HeraclesProto.MessageIdData;
import com.song.heracles.net.proto.HeraclesProto.PullOffsetRequest;
import com.song.heracles.net.proto.HeraclesProto.PullOffsetResponse;
import io.netty.buffer.Unpooled;
import io.vertx.core.Future;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.distributedlog.DLSN;

/**
 * @author song
 */
@Slf4j
public class ServerMessageProcessor extends HeraclesApiGrpc.HeraclesApiVertxImplBase {

    private final BrokerService brokerService;

    private final ConcurrentMap<Long, CompletableFuture<Producer>> producers = new ConcurrentHashMap<>();

    private final ConcurrentMap<Long, CompletableFuture<Consumer>> consumers = new ConcurrentHashMap<>();

    public ServerMessageProcessor(BrokerService brokerService) {
        this.brokerService = brokerService;
    }

    @Override
    public void handleProducerConnect(HeraclesProto.ProducerConnectRequest request,
        Future<HeraclesProto.ProducerResponse> response) {
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
        CompletableFuture<Producer> existingProducerFuture = producers
            .putIfAbsent(producerId, producerFuture);
        if (existingProducerFuture != null) {
            if (existingProducerFuture.isDone() && !existingProducerFuture
                .isCompletedExceptionally()) {
                Producer producer = existingProducerFuture.getNow(null);
                log.info("Producer with the same producerId is already created: {}", producer);
                HeraclesProto.ProducerResponse producerResponse = HeraclesProto.ProducerResponse
                    .newBuilder()
                    .setProducerId(producerId)
                    .setProducerName(producerName)
                    .build();
                response.complete(producerResponse);
            } else {
                log.warn("Producer[{}-{}] is already on the connection.", topic, producerId);
                ErrorCode errorCode =
                    !existingProducerFuture.isDone() ? ErrorCode.STREAM_NOT_READY :
                        getErrorCode(existingProducerFuture);
                response.fail(new HeraclesException("Producer is already present on the connection",
                    errorCode.getCode()));
            }
        } else {
            ProducerManager producerManager = brokerService.getProducerManager();
            String finalProducerName = producerName;
            producerManager.create(topic)
                .thenAccept(producer -> {
                    HeraclesProto.ProducerResponse producerResponse = HeraclesProto.ProducerResponse
                        .newBuilder()
                        .setProducerName(finalProducerName)
                        .setProducerId(producerId)
                        .build();
                    log.info("Created producer :{}.", producer);
                    response.complete(producerResponse);
                    producerFuture.complete(producer);
                })
                .exceptionally(throwable -> {
                    log.error("Failed to create producer :" + topic, throwable);
                    producerFuture.completeExceptionally(throwable);
                    producers.remove(producerId);
                    response.fail(
                        new HeraclesException("Create producer failed:" + throwable.getMessage(),
                            ErrorCode.UNKNOWN_ERROR.getCode()));
                    return null;
                });
        }
    }

    @Override
    public void handleSendMessage(HeraclesProto.SendMessageRequest request,
        Future<HeraclesProto.SendMessageResponse> response) {
        HeraclesProto.Message message = request.getMessage();
        ByteString body = message.getBody();
        long producerId = request.getProducerId();
        CompletableFuture<Producer> producerFuture = producers.get(producerId);
        if (producerFuture == null || !producerFuture.isDone() || producerFuture
            .isCompletedExceptionally()) {
            log.warn("Producer is already closed or not ready :[{}]", producerId);
            response.fail(String.format("Producer is already closed or not ready :%d", producerId));
            return;
        }
        Producer producer = producerFuture.getNow(null);
        producer.sendAsync(Unpooled.wrappedBuffer(body.toByteArray())).thenAccept(dlsn -> {
            HeraclesProto.SendMessageResponse sendMessageResponse = HeraclesProto.SendMessageResponse
                .newBuilder()
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

    @Override
    public void handleConsumerConnect(ConsumerConnectRequest request,
        Future<ConsumerConnectResponse> response) {
        String topic = request.getTopic();
        long consumerId = request.getConsumerId();
        String consumerName = request.getConsumerName();
        if (StringUtils.isBlank(consumerName)) {
            consumerName = brokerService.getConsumerNameGenerator().getNextId();
        }
        Result result = ValidateUtils.checkTopicName(topic);
        if (!result.isSuccess()) {
            response.fail(result.getMessage());
            return;
        }
        CompletableFuture<Consumer> producerFuture = new CompletableFuture<>();
        CompletableFuture<Consumer> previousFuture = consumers
            .putIfAbsent(consumerId, producerFuture);
        if (previousFuture != null) {
            if (previousFuture.isDone() && !previousFuture.isCompletedExceptionally()) {
                Consumer consumer = previousFuture.getNow(null);
                log.info("Consumer with the same consumerId is already created: {}", consumer);
                ConsumerConnectResponse connectResponse = ConsumerConnectResponse.newBuilder()
                    .setConsumerId(consumerId)
                    .setConsumerName(consumerName)
                    .build();
                response.complete(connectResponse);
            } else {
                log.warn("Consumer[{}-{}] is already on the connection.", topic, consumerId);
                ErrorCode errorCode =
                    previousFuture.isDone() ? getErrorCode(previousFuture)
                        : ErrorCode.STREAM_NOT_READY;
                response.fail(
                    new HeraclesException("Consumer is already on the connection",
                        errorCode.getCode()));
            }
        } else {
            ConsumerManager consumerManager = brokerService.getConsumerManager();
            String finalConsumerName = consumerName;
            consumerManager.create(topic).thenAccept(consumer -> {
                producerFuture.complete(consumer);
                response.complete(ConsumerConnectResponse.newBuilder()
                    .setConsumerId(consumerId)
                    .setConsumerName(finalConsumerName)
                    .build());
            }).exceptionally(throwable -> {
                log.error("Failed to create consumer :" + topic, throwable);
                response
                    .fail(new HeraclesException("Create consumer failed:" + throwable.getMessage(),
                        ErrorCode.UNKNOWN_ERROR.getCode()));
                producerFuture.completeExceptionally(throwable);
                producers.remove(consumerId);
                return null;
            });
        }
    }

    @Override
    public void handleConsumerPullMessage(ConsumerPullMessageRequest request,
        Future<ConsumerPullMessageResponse> response) {
        long consumerId = request.getConsumerId();
        int maxMessage = request.getMaxMessage();
        CompletableFuture<Consumer> consumerFuture = consumers.get(consumerId);
        if (consumerFuture == null || !consumerFuture.isDone() || consumerFuture
            .isCompletedExceptionally()) {
            log.warn("Consumer is already closed or not ready :[{}]", consumerFuture);
            response.fail(String.format("Producer is already closed or not ready :%d", consumerId));
            return;
        }
        Consumer consumer = consumerFuture.getNow(null);
        consumer.pullMessages(maxMessage).thenAccept(messages -> {
            Builder pullMessageResponseBuilder = ConsumerPullMessageResponse.newBuilder()
                .setConsumerId(consumerId);
            messages.forEach(message -> {
                Offset offset = message.getOffset();
                DLSN dlsn = offset.getDlsn();
                Message.newBuilder()
                    .setBody(ByteString.copyFrom(message.getPayload()))
                    .setOffset(MessageIdData.newBuilder()
                        .setLogSegmentSequenceNo(dlsn.getLogSegmentSequenceNo())
                        .setEntryId(dlsn.getEntryId())
                        .setSlotId(dlsn.getEntryId())
                        .build());
                pullMessageResponseBuilder.addMessages(Message.newBuilder()
                    .setBody(ByteString.copyFrom(message.getPayload()))
                    .setOffset(MessageIdData.newBuilder()
                        .setLogSegmentSequenceNo(dlsn.getLogSegmentSequenceNo())
                        .setEntryId(dlsn.getEntryId())
                        .setSlotId(dlsn.getEntryId())
                        .build())
                    .build());
            });
            if (!messages.isEmpty()) {
                com.song.heracles.broker.core.message.Message message = messages
                    .get(messages.size() - 1);
                if (message != null) {
                    DLSN nextDLSN = message.getOffset().getDlsn().getNextDLSN();
                    if (nextDLSN != null) {
                        MessageIdData nextPullOffset = MessageIdData.newBuilder()
                            .setLogSegmentSequenceNo(nextDLSN.getLogSegmentSequenceNo())
                            .setEntryId(nextDLSN.getEntryId())
                            .setSlotId(nextDLSN.getSlotId())
                            .build();
                        pullMessageResponseBuilder.setNextPullOffset(nextPullOffset);
                    }
                }
            }
            response.complete(pullMessageResponseBuilder.build());
        }).exceptionally(throwable -> {
            log.error("Pull message failed.", throwable);
            response.fail(new HeraclesException("Pulling message failed:" + throwable.getMessage(),
                getErrorCode(throwable).getCode()));
            return null;
        });
    }

    @Override
    public void handlePullOffset(PullOffsetRequest request, Future<PullOffsetResponse> response) {
        long consumerId = request.getConsumerId();
        CompletableFuture<Consumer> consumerFuture = consumers.get(consumerId);
        if (consumerFuture == null || !consumerFuture.isDone() || consumerFuture
            .isCompletedExceptionally()) {
            log.warn("Consumer is already closed or not ready :[{}]", consumerFuture);
            response.fail(String.format("Producer is already closed or not ready :%d", consumerId));
            return;
        }
        Consumer consumer = consumerFuture.getNow(null);
        OffsetStorage offsetStorage = brokerService.getOffsetStorage();
        try {
            Offset offset = offsetStorage.readOffsetFromCache(consumer.getPartitionedTopic());
            DLSN dlsn = offset.getDlsn();
            MessageIdData messageIdData = MessageIdData.newBuilder()
                .setLogSegmentSequenceNo(dlsn.getLogSegmentSequenceNo())
                .setEntryId(dlsn.getEntryId())
                .setSlotId(dlsn.getSlotId())
                .build();
            response.complete(PullOffsetResponse.newBuilder()
                .setOffset(messageIdData)
                .build());
        } catch (Exception e) {
            log.error("Read offset failed.", e);
            response.fail(new HeraclesException(
                "Read offset for :" + consumer.getPartitionedTopic().getOriginalTopic()
                    + " failed.",
                getErrorCode(e).getCode()));
        }
    }

    private <T> ErrorCode getErrorCode(CompletableFuture<T> future) {
        ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;
        try {
            future.getNow(null);
        } catch (Exception e) {
            log.error("Try to get from future :" + e.getMessage(), e);
            if (e.getCause() instanceof HeraclesException) {
                errorCode = ErrorCode.getByCode(((HeraclesException) e.getCause()).getCode());
            }
        }
        return errorCode;
    }

    public ErrorCode getErrorCode(Throwable throwable) {
        ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;
        if (throwable.getCause() instanceof HeraclesException) {
            errorCode = ErrorCode.getByCode(((HeraclesException) throwable.getCause()).getCode());
        }
        return errorCode;
    }

}
