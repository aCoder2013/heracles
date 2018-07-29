package com.song.heracles.client.api;

import com.song.heracles.client.exception.HeraclesClientException;
import com.song.heracles.client.message.MessageId;
import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

/**
 * @author song
 */
public interface Producer extends Closeable {

    void start() throws InterruptedException, HeraclesClientException;

    String getTopic();

    String producerName();

    MessageId send(byte[] message) throws InterruptedException, HeraclesClientException;

    CompletableFuture<MessageId> sendAsync(byte[] message);

}
