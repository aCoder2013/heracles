package com.song.heracles.broker.service.support;

import static org.junit.Assert.assertEquals;

import com.song.heracles.broker.service.DistributedIdGenerator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author song
 */
public class ZkDistributedIdGeneratorTest {

    private CuratorFramework client;

    private DistributedIdGenerator distributedIdGenerator;

    @Before
    public void setUp() throws Exception {
        TestingServer zookeeper = new TestingServer(2182, false);
        zookeeper.start();
        client = CuratorFrameworkFactory.newClient("127.0.0.1:2182", new RetryNTimes(3, 1000));
        client.start();
        client.blockUntilConnected();
    }

    @Test
    public void getNextId() throws Exception {
        distributedIdGenerator = new ZkDistributedIdGenerator(client, "/counters/producer-names",
            "producer");
        assertEquals("producer-0-1", distributedIdGenerator.getNextId());
        assertEquals("producer-0-2", distributedIdGenerator.getNextId());
        assertEquals("producer-0-3", distributedIdGenerator.getNextId());
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }
}