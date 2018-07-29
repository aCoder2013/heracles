package com.song.heracles.store.core;

import com.song.heracles.common.util.IdGenerator;
import java.util.concurrent.CompletableFuture;
import org.apache.distributedlog.api.AsyncLogWriter;

/**
 * @author song
 */
public interface StreamOp {

    String streamName();

    /**
     * execute the given operation
     */
    CompletableFuture<Void> execute(AsyncLogWriter asyncLogWriter, IdGenerator idGenerator);

    /**
     * Abort this operation with given error.
     */
    void fail(Throwable throwable);
}
