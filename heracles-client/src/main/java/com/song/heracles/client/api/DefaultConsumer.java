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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultConsumer implements Consumer {

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
  public void start() throws InterruptedException ,HeraclesClientException{
    Result<Void> result = new Result<>();
    CountDownLatch latch = new CountDownLatch(1);
    ConsumerConnectRequest request = ConsumerConnectRequest.newBuilder()
        .setConsumerId(consumerId)
        .setConsumerName(consumerName)
        .setTopic(topic)
        .build();
    heraclesClient.handleConsumerConnect(request,asyncResult -> {
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
    // TODO: 2018/6/10 Add timeout
    latch.await();
    if (result.getThrowable() != null) {
      throw new HeraclesClientException(result.getThrowable());
    }
  }

  @Override
  public Message receive() throws HeraclesClientException, InterruptedException {
    class Result {
      Message message = null;
      Throwable throwable;
    }
    CountDownLatch latch = new CountDownLatch(1);
    Result result = new Result();
    ConsumerPullMessageRequest request = ConsumerPullMessageRequest.newBuilder()
        .setConsumerId(consumerId)
        .setMaxMessage(1)
        .setOffset(MessageIdData.newBuilder().setLogSegmentSequenceNo(0).setEntryId(0).setSlotId(0))
        .build();
    heraclesClient.handleConsumerPullMessage(request,asyncResult -> {
      if(asyncResult.succeeded()){
        ConsumerPullMessageResponse pullMessageResponse = asyncResult.result();
        List<HeraclesProto.Message> messages = pullMessageResponse.getMessagesList();
        if(messages != null && messages.size() > 0){
          HeraclesProto.Message remoteMessage = messages.get(0);
          MessageIdData offset = remoteMessage.getOffset();
          byte[] body = remoteMessage.getBody().toByteArray();
          Message message = new Message(new MessageId(offset.getLogSegmentSequenceNo(),offset.getEntryId(),offset.getSlotId()),body);
          result.message = message;
        }
      }else {
        result.throwable = asyncResult.cause();
      }
      latch.countDown();
    });
    latch.await();
    return result.message;
  }

  @Override
  public void close() throws IOException {

  }
}
