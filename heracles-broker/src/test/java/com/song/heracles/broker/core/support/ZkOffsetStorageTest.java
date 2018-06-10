package com.song.heracles.broker.core.support;

import com.song.heracles.broker.core.Offset;
import com.song.heracles.broker.core.OffsetStorage;
import com.song.heracles.broker.core.PartitionedTopic;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.test.TestingServer;
import org.apache.distributedlog.DLSN;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author song
 */
public class ZkOffsetStorageTest {

	private OffsetStorage offsetStorage;
	private TestingServer zookeeper;
	private CuratorFramework curatorFramework;

	@Before
	public void setUp() throws Exception {
		zookeeper = new TestingServer(2182, false);
		zookeeper.start();
		curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1:2182", new RetryNTimes(3, 1000));
		curatorFramework.start();
		offsetStorage = new ZkOffsetStorage(curatorFramework);
		offsetStorage.start();
	}

	@Test
	public void readOffsetFromCache() throws Exception {
		PartitionedTopic partitionedTopic = new PartitionedTopic("base-topic", 0);
		try {
			Offset offset = offsetStorage.readOffsetFromCache(partitionedTopic);
			assertThat(offset.getDlsn()).isEqualTo(DLSN.InitialDLSN);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void readOffsetFromStorage() throws Exception {
		PartitionedTopic partitionedTopic = new PartitionedTopic("base-topic", 0);
		Offset offset = new Offset(new DLSN(1, 2, 3));
		this.offsetStorage.updateOffset(partitionedTopic, offset);
		assertThat(this.offsetStorage.readOffsetFromStorage(partitionedTopic).getDlsn()).isEqualTo(DLSN.InitialDLSN);

		this.offsetStorage.persistOffset(partitionedTopic);
		assertThat(this.offsetStorage.readOffsetFromStorage(partitionedTopic).getDlsn()).isEqualTo(new DLSN(1, 2, 3));
	}

	@Test
	public void updateOffset() throws Exception {
		PartitionedTopic partitionedTopic = new PartitionedTopic("base-topic", 0);
		Offset offset = new Offset(new DLSN(1, 2, 3));
		this.offsetStorage.updateOffset(partitionedTopic, offset);
		offset = this.offsetStorage.readOffsetFromCache(partitionedTopic);
		assertThat(offset.getDlsn()).isEqualTo(new DLSN(1, 2, 3));

		this.offsetStorage.updateOffset(partitionedTopic, new Offset(new DLSN(3, 2, 1)));
		offset = this.offsetStorage.readOffsetFromCache(partitionedTopic);
		assertThat(offset.getDlsn()).isEqualTo(new DLSN(3, 2, 1));
	}

	@Test
	public void persistOffset() throws Exception {
		PartitionedTopic partitionedTopic = new PartitionedTopic("base-topic", 0);
		Offset offset = new Offset(new DLSN(1, 2, 3));
		this.offsetStorage.updateOffset(partitionedTopic, offset);
		this.offsetStorage.persistOffset(partitionedTopic);
		offset = this.offsetStorage.readOffsetFromStorage(partitionedTopic);
		assertThat(offset.getDlsn()).isEqualTo(new DLSN(1, 2, 3));
	}

	@Test
	public void close() throws IOException {
	}

	@After
	public void tearDown() throws Exception {
		offsetStorage.close();
		curatorFramework.close();
		zookeeper.close();
	}
}