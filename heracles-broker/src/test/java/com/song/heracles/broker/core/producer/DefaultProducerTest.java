package com.song.heracles.broker.core.producer;

import static org.assertj.core.api.Assertions.assertThat;

import com.song.heracles.broker.core.PartitionedTopic;
import com.song.heracles.common.concurrent.OrderedExecutor;
import com.song.heracles.common.exception.HeraclesException;
import com.song.heracles.store.core.StreamFactory;
import com.song.heracles.store.core.support.DefaultStreamFactory;
import io.netty.buffer.Unpooled;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.distributedlog.DLSN;
import org.apache.distributedlog.DistributedLogConfiguration;
import org.apache.distributedlog.DistributedLogConstants;
import org.apache.distributedlog.api.namespace.Namespace;
import org.apache.distributedlog.api.namespace.NamespaceBuilder;
import org.apache.distributedlog.common.concurrent.FutureUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author song
 */
@Slf4j
public class DefaultProducerTest {

    private Producer producer;
    private StreamFactory streamFactory;

    private Namespace namespace;

    @Before
    public void setUp() throws Exception {
        DistributedLogConfiguration dlConfig = new DistributedLogConfiguration();
        dlConfig.setImmediateFlushEnabled(true);
        dlConfig.setOutputBufferSize(0);
        dlConfig.setPeriodicFlushFrequencyMilliSeconds(0);
        dlConfig.setLockTimeout(DistributedLogConstants.LOCK_IMMEDIATE);
        namespace = NamespaceBuilder.newBuilder()
                .conf(dlConfig)
                .uri(URI.create("distributedlog://127.0.0.1:7000/messaging/my_namespace"))
                .regionId(DistributedLogConstants.LOCAL_REGION_ID)
                .clientId("default")
                .build();
        streamFactory = new DefaultStreamFactory("default", dlConfig, namespace, OrderedExecutor.newBuilder().build());
        PartitionedTopic partitionedTopic = new PartitionedTopic("messaging-stream", 1);
        String originalTopic = partitionedTopic.getOriginalTopic();
        producer = new DefaultProducer(partitionedTopic, streamFactory.getOrOpenStream(originalTopic));
        CountDownLatch latch = new CountDownLatch(1);
        producer.start().thenRun(latch::countDown).exceptionally(throwable -> {
            throwable.printStackTrace();
            latch.countDown();
            return null;
        });
        latch.await();
    }

    @Test(timeout = 5000L)
    public void send() {
        try {
            DLSN dlsn = producer.send(Unpooled.wrappedBuffer("Hello World".getBytes()));
            assertThat(dlsn).isNotNull();
            System.out.println(dlsn);
        } catch (InterruptedException | HeraclesException e) {
            e.printStackTrace();
        }
    }

    @Test(timeout = 5000L)
    public void sendAsync() throws HeraclesException, InterruptedException {
        for (int i = 0; i < 100; i++) {
            CountDownLatch latch = new CountDownLatch(1);
            producer.sendAsync(Unpooled.wrappedBuffer("Hello World".getBytes()))
                    .thenAccept(dlsn -> {
                        assertThat(dlsn).isNotNull();
                        latch.countDown();
                    }).exceptionally(throwable -> {
                throwable.printStackTrace();
                latch.countDown();
                return null;
            });
            latch.await();
        }
    }

    @After
    public void tearDown() throws Exception {
        producer.close();
        namespace.close();
        FutureUtils.ignore(streamFactory.closeStreams());
    }
}