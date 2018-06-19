package com.song.heracles.client.api;

import com.song.heracles.client.exception.HeraclesClientException;
import java.io.Closeable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Consumer extends Closeable {


  /**
   * Get topic of the consumer
   *
   * @return topic of the consumer
   */
  String getTopic();

  /**
   * Start the consumer
   */
  void start() throws InterruptedException, HeraclesClientException;

  List<Message> receive() throws HeraclesClientException, InterruptedException;

  CompletableFuture<List<Message>> receiveAsync();
}

