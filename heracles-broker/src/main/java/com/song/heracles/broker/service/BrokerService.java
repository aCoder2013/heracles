package com.song.heracles.broker.service;

import com.song.heracles.broker.config.BrokerConfiguration;
import com.song.heracles.broker.core.ProducerManager;
import com.song.heracles.broker.core.processor.ServerMessageProcessor;
import com.song.heracles.broker.service.support.ZkDistributedIdGenerator;
import com.song.heracles.common.concurrent.OrderedExecutor;
import com.song.heracles.common.constants.ErrorCode;
import com.song.heracles.common.exception.HeraclesException;
import com.song.heracles.common.util.NetUtils;
import com.song.heracles.net.RemotingServer;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.distributedlog.DistributedLogConfiguration;
import org.apache.distributedlog.DistributedLogConstants;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.vertx.core.Vertx;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author song
 */
@Slf4j
public class BrokerService implements Closeable {

	private final static String PRODUCER_NAME_PATTERN = "/counters/producer-name";

	private AtomicReference<State> state = new AtomicReference<>(State.UNINITIALIZED);

	private BrokerConfiguration brokerConfiguration = null;

	private DistributedIdGenerator producerNameGenerator = null;

	private Vertx vertx;

	private VertxServer vertxServer;

	private RemotingServer remotingServer;

	private CuratorFramework curatorFramework;

	private DistributedLogConfiguration dLogConfig;

	private OrderedExecutor orderedExecutor;

	private ProducerManager producerManager;

	private Lock lock = new ReentrantLock();

	private Condition isClosedCondition = lock.newCondition();

	public BrokerService(BrokerConfiguration brokerConfiguration) {
		this.brokerConfiguration = brokerConfiguration;
	}

	public void start() throws HeraclesException {
		if (state.compareAndSet(State.UNINITIALIZED, State.INITIALIZING)) {
			log.info("Start to initialize broker service.");
			try {
				vertx = Vertx.vertx();
				vertxServer = VertxServerBuilder
					.forAddress(vertx, StringUtils.defaultIfBlank(brokerConfiguration.getBindAddress(), NetUtils.getLocalhost()), brokerConfiguration.getBrokerPort())
					.addService(new ServerMessageProcessor(this))
					.build();
				remotingServer = new RemotingServer(vertxServer);
				remotingServer.start();
				curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1:2181", brokerConfiguration.getZooKeeperSessionTimeoutMillis(),
					brokerConfiguration.getZookeeperConnectionTimeout(), new RetryNTimes(3, 3000));
				curatorFramework.start();
				producerNameGenerator = new ZkDistributedIdGenerator(curatorFramework, PRODUCER_NAME_PATTERN, brokerConfiguration.getClusterName());
				dLogConfig = new DistributedLogConfiguration();
				dLogConfig.setImmediateFlushEnabled(true);
				dLogConfig.setOutputBufferSize(0);
				dLogConfig.setPeriodicFlushFrequencyMilliSeconds(0);
				dLogConfig.setLockTimeout(DistributedLogConstants.LOCK_IMMEDIATE);
				orderedExecutor = OrderedExecutor.newBuilder()
					.name("heracles-broker-pool")
					.numThreads(20)
					.build();
				producerManager = new ProducerManager(this);
				producerManager.start();
				log.info("Finish to initialize broker service.");
				this.state.set(State.INITIALIZED);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new HeraclesException(e);
			}
		} else {
			throw new HeraclesException("Can't start broker service ,current state is :" + state.get(), ErrorCode.ILLEGAL_STATE.getCode());
		}
	}

	public ProducerManager getProducerManager() {
		return producerManager;
	}

	public BrokerConfiguration getBrokerConfiguration() {
		return brokerConfiguration;
	}

	public DistributedIdGenerator getProducerNameGenerator() {
		return producerNameGenerator;
	}

	public DistributedLogConfiguration getdLogConfig() {
		return dLogConfig;
	}

	public OrderedExecutor getOrderedExecutor() {
		return orderedExecutor;
	}

	@Override
	public void close() throws IOException {
		lock.lock();
		try {
			if (state.compareAndSet(State.INITIALIZED, State.CLOSING)) {
				if (vertx != null) {
					vertx.close();
				}

				if (remotingServer != null) {
					remotingServer.shutdown();
				}

				if (vertxServer != null) {
					vertxServer.shutdown();
				}

				if (curatorFramework != null) {
					curatorFramework.close();
				}
				state.set(State.CLOSED);
				log.info("Close broker service.");
			} else {
				log.warn("Can't close broker service ,current state is :" + state.get());
			}
		} finally {
			lock.unlock();
		}
	}

	public void waitUntilClosed() throws InterruptedException {
		lock.lock();
		try {
			while (state.get() != State.CLOSED) {
				isClosedCondition.await();
			}
		} finally {
			lock.unlock();
		}
	}

	public enum State {
		UNINITIALIZED, INITIALIZING, INITIALIZED, CLOSING, CLOSED
	}
}
