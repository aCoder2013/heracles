package com.song.heracles.broker.service.support;

import com.song.heracles.broker.service.DistributedIdGenerator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author song
 */
public class ZkDistributedIdGeneratorTest {

	private CuratorFramework client;

	private DistributedIdGenerator distributedIdGenerator;

	@Before
	public void setUp() throws Exception {
		TestingServer zookeeper = new TestingServer(2181, false);
		zookeeper.start();
		client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new RetryNTimes(3, 1000));
		client.start();
	}

	@Test
	public void getNextId() throws Exception {
		distributedIdGenerator = new ZkDistributedIdGenerator(client, "/counters/producer-names", "producer");
		assertEquals("producer-0-1", distributedIdGenerator.getNextId());
		assertEquals("producer-0-2", distributedIdGenerator.getNextId());
		assertEquals("producer-0-3", distributedIdGenerator.getNextId());
	}

	@After
	public void tearDown() throws Exception {
		client.close();
	}
}