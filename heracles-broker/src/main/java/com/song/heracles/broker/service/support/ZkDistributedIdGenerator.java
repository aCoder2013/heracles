package com.song.heracles.broker.service.support;

import com.song.heracles.broker.service.DistributedIdGenerator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;

/**
 * @author song
 */
@Slf4j
public class ZkDistributedIdGenerator implements DistributedIdGenerator {

	private final String prefix;

	private final int instanceId;

	private final AtomicLong counter = new AtomicLong();

	public ZkDistributedIdGenerator(CuratorFramework client, String counterPath, String prefix) throws Exception {
		this.prefix = prefix;
		String createPath = client
			.create()
			.creatingParentsIfNeeded()
			.withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
			.withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
			.forPath(counterPath + "/-", new byte[0]);

		String[] parts = createPath.split("/");
		String name = parts[parts.length - 1].replace('-', ' ').trim();

		this.instanceId = Integer.parseInt(name);
		log.info("Created ephemeral sequential node at {} -- Generator Id is {}-{}", createPath, prefix, instanceId);
	}

	@Override
	public String getNextId() {
		return String.format("%s-%d-%d", prefix, instanceId, counter.incrementAndGet());
	}
}
