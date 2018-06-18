package com.song.heracles.client.api;

import com.song.heracles.client.configuration.ClientConfiguration;
import com.song.heracles.client.exception.HeraclesClientException;
import com.song.heracles.common.util.GsonUtils;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import org.junit.Before;
import org.junit.Test;

public class DefaultConsumerTest {



  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void receive() throws HeraclesClientException, InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    ClientConfiguration clientConfiguration = ClientConfiguration.builder()
        .servers(Collections.singletonList("localhost:7160"))
        .build();
    HeraclesClient heraclesClient = new HeraclesClient(clientConfiguration);
    Consumer consumer = heraclesClient.createConsumer("messaging-stream-1", "test-consumer");
    consumer.start();
    Message message = consumer.receive();
    System.out.println(GsonUtils.toJson(message));
    System.out.println(new String(message.getBody()));
  }
}