package com.song.heracles.client.api;

import com.song.heracles.client.configuration.ClientConfiguration;
import com.song.heracles.client.configuration.ProducerConfiguration;
import com.song.heracles.client.exception.HeraclesClientException;

import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;

/**
 * @author song
 */
public class DefaultProducerTest {

	@Test
	public void connect() throws InterruptedException, HeraclesClientException {
		ClientConfiguration clientConfiguration = ClientConfiguration.builder()
			.servers(Collections.singletonList("localhost:7160"))
			.build();
		HeraclesClient heraclesClient = new HeraclesClient(clientConfiguration);
		Producer producer = heraclesClient.createProducer(ProducerConfiguration.builder()
			.topic("messaging-stream-1")
			.build());
		producer.start();
		CountDownLatch latch = new CountDownLatch(1);
		producer.sendAsync("Hello World".getBytes()).thenAccept(messageId -> {
			System.out.println(messageId);
			latch.countDown();
		}).exceptionally(throwable -> {
			throwable.printStackTrace();
			latch.countDown();
			return null;
		});
		latch.await();
	}
}