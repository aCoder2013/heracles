package com.song.heracles.client.api;

import com.song.heracles.client.configuration.ClientConfiguration;
import com.song.heracles.client.configuration.ConsumerConfiguration;
import com.song.heracles.client.exception.HeraclesClientException;
import com.song.heracles.common.util.GsonUtils;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.junit.Before;
import org.junit.Test;

public class DefaultConsumerTest {


  private Consumer consumer;

  @Before
  public void setUp() throws Exception {
    ClientConfiguration clientConfiguration = ClientConfiguration.builder()
        .servers(Collections.singletonList("localhost:7160"))
        .build();
    HeraclesClient heraclesClient = new HeraclesClient(clientConfiguration);
    consumer = heraclesClient.createConsumer(ConsumerConfiguration.builder()
        .topic("messaging-stream-1")
        .consumerName("test-consumer")
        .build());
    consumer.start();
  }

  @Test(timeout = 5000)
  public void receive() throws HeraclesClientException, InterruptedException {
    List<Message> messages = consumer.receive();
    messages.forEach(message -> {
      System.out.println(GsonUtils.toJson(message));
      System.out.println(new String(message.getBody()));
    });
  }

  @Test(timeout = 5000)
  public void receiveAsync() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    consumer.receiveAsync().thenAccept(messages -> {
      messages.forEach(message -> {
        System.out.println(GsonUtils.toJson(message));
        System.out.println(new String(message.getBody()));
      });
      latch.countDown();
    }).exceptionally(throwable -> {
      throwable.printStackTrace();
      latch.countDown();
      return null;
    });
    latch.await();
  }
}