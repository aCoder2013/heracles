package com.song.heracles.client.api;

import com.song.heracles.client.exception.HeraclesClientException;
import com.song.heracles.common.util.Result;
import com.song.heracles.net.proto.HeraclesApiGrpc;
import com.song.heracles.net.proto.HeraclesProto;
import com.song.heracles.net.proto.HeraclesProto.ConsumerConnectRequest;
import com.song.heracles.net.proto.HeraclesProto.ConsumerConnectResponse;
import com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageRequest;
import com.song.heracles.net.proto.HeraclesProto.ConsumerPullMessageResponse;
import com.song.heracles.net.proto.HeraclesProto.MessageIdData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultConsumer implements Consumer {

  private static final int DEFAULT_MAX_PULL_MESSAGE_NUMBER = 100;

  private static final long DEFAULT_TIMEOUT_SECOND = 6;

  private String topic;

  private long consumerId;

  private String consumerName;

  private final HeraclesApiGrpc.HeraclesApiVertxStub heraclesClient;

  public DefaultConsumer(String topic, String consumerName,
      HeraclesApiGrpc.HeraclesApiVertxStub heraclesClient, long consumerId) {
    this.topic = topic;
    this.consumerId = consumerId;
    this.consumerName = consumerName;
    this.heraclesClient = heraclesClient;
  }

  @Override
  public String getTopic() {
    return this.topic;
  }

  @Override
  public void start() throws InterruptedException, HeraclesClientException {
    Result<Void> result = new Result<>();
    CountDownLatch latch = new CountDownLatch(1);
    ConsumerConnectRequest request = ConsumerConnectRequest.newBuilder()
        .setConsumerId(consumerId)
        .setConsumerName(consumerName)
        .setTopic(topic)
        .build();
    heraclesClient.handleConsumerConnect(request, asyncResult -> {
      if (asyncResult.succeeded()) {
        ConsumerConnectResponse connectResponse = asyncResult.result();
        consumerId = connectResponse.getConsumerId();
        consumerName = connectResponse.getConsumerName();
        log.info("Consumer started successfully.");
      } else {
        result.setThrowable(asyncResult.cause());
      }
      latch.countDown();
    });
    latch.await(DEFAULT_TIMEOUT_SECOND, TimeUnit.SECONDS);
    if (result.getThrowable() != null) {
      throw new HeraclesClientException(result.getThrowable());
    }
  }

  @Override
  public List<Message> receive() throws HeraclesClientException, InterruptedException {
    class Result {

      private List<Message> messages = null;
      private Throwable throwable;
    }
    CountDownLatch latch = new CountDownLatch(1);
    Result result = new Result();
    ConsumerPullMessageRequest request = ConsumerPullMessageRequest.newBuilder()
        .setConsumerId(consumerId)
        .setMaxMessage(DEFAULT_MAX_PULL_MESSAGE_NUMBER)
        .setOffset(MessageIdData.newBuilder().setLogSegmentSequenceNo(2).setEntryId(0).setSlotId(0))
        .build();
    heraclesClient.handleConsumerPullMessage(request, asyncResult -> {
      if (asyncResult.succeeded()) {
        ConsumerPullMessageResponse pullMessageResponse = asyncResult.result();
        List<HeraclesProto.Message> messages = pullMessageResponse.getMessagesList();
        if (messages != null && messages.size() > 0) {
          result.messages = convertToMessage(messages);
        } else {
          result.messages = Collections.emptyList();
        }
        latch.countDown();
      }else {
        result.throwable = asyncResult.cause();
      }
    });
    latch.await();
    if (result.throwable != null) {
      throw new HeraclesClientException("Pull message failed:", result.throwable);
    }
    return result.messages;
  }

  @Override
  public CompletableFuture<List<Message>> receiveAsync() {
    CompletableFuture<List<Message>> future = new CompletableFuture<>();
    ConsumerPullMessageRequest request = ConsumerPullMessageRequest.newBuilder()
        .setConsumerId(consumerId)
        .setMaxMessage(DEFAULT_MAX_PULL_MESSAGE_NUMBER)
        .setOffset(MessageIdData.newBuilder().setLogSegmentSequenceNo(0).setEntryId(0).setSlotId(0))
        .build();
    heraclesClient.handleConsumerPullMessage(request, asyncResult -> {
      if (asyncResult.succeeded()) {
        ConsumerPullMessageResponse pullMessageResponse = asyncResult.result();
        List<HeraclesProto.Message> messages = pullMessageResponse.getMessagesList();
        if (messages != null && messages.size() > 0) {
          List<Message> messageList = convertToMessage(messages);
          future.complete(messageList);
        } else {
          future.complete(Collections.emptyList());
        }
      } else {
        future.completeExceptionally(
            new HeraclesClientException("Pull Message failed.", asyncResult.cause()));
      }
    });
    return future;
  }

  private List<Message> convertToMessage(List<HeraclesProto.Message> messages) {
    List<Message> messageList = new ArrayList<>(messages.size());
    messages.forEach(remotingMessage -> {
      MessageIdData offset = remotingMessage.getOffset();
      byte[] body = remotingMessage.getBody().toByteArray();
      Message message = new Message(
          new MessageId(offset.getLogSegmentSequenceNo(), offset.getEntryId(),
              offset.getSlotId()), body);
      messageList.add(message);
    });
    return messageList;
  }

  @Override
  public void close() throws IOException {

  }
}
