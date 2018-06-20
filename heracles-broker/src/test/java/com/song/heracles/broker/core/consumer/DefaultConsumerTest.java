package com.song.heracles.broker.core.consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.song.heracles.broker.core.Message;
import com.song.heracles.broker.core.OffsetStorage;
import com.song.heracles.broker.core.PartitionedTopic;
import com.song.heracles.broker.core.support.ZkOffsetStorage;
import com.song.heracles.common.concurrent.OrderedExecutor;
import com.song.heracles.store.core.StreamFactory;
import com.song.heracles.store.core.support.DefaultStreamFactory;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.distributedlog.DistributedLogConfiguration;
import org.apache.distributedlog.DistributedLogConstants;
import org.apache.distributedlog.api.namespace.Namespace;
import org.apache.distributedlog.api.namespace.NamespaceBuilder;
import org.apache.distributedlog.common.concurrent.FutureUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DefaultConsumerTest {

    private Consumer consumer;

    private StreamFactory streamFactory;

    private Namespace namespace;

    private OffsetStorage offsetStorage;

    private CuratorFramework curatorFramework;

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
        curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new RetryNTimes(3, 1000));
        curatorFramework.start();
        offsetStorage = new ZkOffsetStorage(curatorFramework);
        offsetStorage.start();
        consumer = new DefaultConsumer(partitionedTopic, streamFactory.getOrOpenStream(originalTopic), offsetStorage);
        CountDownLatch latch = new CountDownLatch(1);
        consumer.start().thenRun(latch::countDown).exceptionally(throwable -> {
            throwable.printStackTrace();
            latch.countDown();
            return null;
        });
        latch.await();
    }

    @Test(timeout = 5000L)
    public void pullMessages() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture<List<Message>> future = consumer.pullMessages(10);
        future.thenAccept(messages -> {
            for (Message message : messages) {
                assertNotNull(message);
                assertNotNull(message.getOffset());
                assertNotNull(message.getPayload());
                assertEquals("Hello World", new String(message.getPayload()));
            }
            latch.countDown();
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            latch.countDown();
            return null;
        });
        latch.await();
    }


    @After
    public void tearDown() throws Exception {
        FutureUtils.result(consumer.close());
        FutureUtils.result(streamFactory.closeStreams());
        offsetStorage.close();
        curatorFramework.close();
    }
}