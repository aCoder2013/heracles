package com.song.heracles.broker.service;

import com.song.heracles.broker.config.BrokerConfiguration;
import com.song.heracles.broker.core.consumer.ConsumerManager;
import com.song.heracles.broker.core.OffsetStorage;
import com.song.heracles.broker.core.producer.ProducerManager;
import com.song.heracles.broker.core.processor.ServerMessageProcessor;
import com.song.heracles.broker.core.support.ZkOffsetStorage;
import com.song.heracles.broker.service.support.ZkDistributedIdGenerator;
import com.song.heracles.common.concurrent.OrderedExecutor;
import com.song.heracles.common.constants.ErrorCode;
import com.song.heracles.common.exception.HeraclesException;
import com.song.heracles.common.util.NetUtils;
import com.song.heracles.net.RemotingServer;
import com.song.heracles.store.core.StreamFactory;
import com.song.heracles.store.core.support.DefaultStreamFactory;
import io.vertx.core.Vertx;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.distributedlog.DistributedLogConfiguration;
import org.apache.distributedlog.DistributedLogConstants;
import org.apache.distributedlog.api.namespace.Namespace;
import org.apache.distributedlog.api.namespace.NamespaceBuilder;
import org.apache.distributedlog.common.concurrent.FutureUtils;

/**
 * @author song
 */
@Slf4j
public class BrokerService implements Closeable {

	private final static String PRODUCER_NAME_PATTERN = "/counters/producer-name";

	private final static String CONSUMER_NAME_PATTERN = "/counters/consumer-name";

	private AtomicReference<State> state = new AtomicReference<>(State.UNINITIALIZED);

	private BrokerConfiguration brokerConfiguration = null;

	private DistributedIdGenerator producerNameGenerator = null;

	private DistributedIdGenerator consumerNameGenerator = null;

	private Vertx vertx;

	private VertxServer vertxServer;

	private RemotingServer remotingServer;

	private CuratorFramework curatorFramework;

	private DistributedLogConfiguration dLogConfig;

	private StreamFactory streamFactory;

  private OffsetStorage offsetStorage;

	private OrderedExecutor orderedExecutor;

	private ProducerManager producerManager;

  private ConsumerManager consumerManager;

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
				consumerNameGenerator = new ZkDistributedIdGenerator(curatorFramework,CONSUMER_NAME_PATTERN,brokerConfiguration.getClientId());
				dLogConfig = new DistributedLogConfiguration();
				//TODO:need some customization here
				dLogConfig.setImmediateFlushEnabled(true);
				dLogConfig.setOutputBufferSize(0);
				dLogConfig.setPeriodicFlushFrequencyMilliSeconds(0);
				dLogConfig.setLockTimeout(DistributedLogConstants.LOCK_IMMEDIATE);
				orderedExecutor = OrderedExecutor.newBuilder()
					.name("heracles-broker-pool")
					.numThreads(20)
					.build();
				//FIXME:Cleanup resources
				Namespace namespace = NamespaceBuilder.newBuilder()
						.conf(dLogConfig)
						.uri(URI.create(brokerConfiguration.getDistributedLogUri()))
						.regionId(DistributedLogConstants.LOCAL_REGION_ID)
						.clientId(brokerConfiguration.getClientId())
						.build();
				streamFactory = new DefaultStreamFactory(brokerConfiguration.getClientId(), dLogConfig, namespace, orderedExecutor);
        offsetStorage = new ZkOffsetStorage(curatorFramework);
        offsetStorage.start();
				producerManager = new ProducerManager(this);
				producerManager.start();
        consumerManager = new ConsumerManager(this);
        consumerManager.start();
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

  public ConsumerManager getConsumerManager() {
    return consumerManager;
  }

  public BrokerConfiguration getBrokerConfiguration() {
		return brokerConfiguration;
	}

	public DistributedIdGenerator getProducerNameGenerator() {
		return producerNameGenerator;
	}

	public DistributedIdGenerator getConsumerNameGenerator(){
    return consumerNameGenerator;
  }

	public DistributedLogConfiguration getdLogConfig() {
		return dLogConfig;
	}

	public OrderedExecutor getOrderedExecutor() {
		return orderedExecutor;
	}

  public StreamFactory getStreamFactory() {
    return streamFactory;
  }

  public OffsetStorage getOffsetStorage() {
    return offsetStorage;
  }

  @Override
	public void close() throws IOException {
		lock.lock();
		try {
			if (state.compareAndSet(State.INITIALIZED, State.CLOSING)) {
				if (vertx != null) {
					vertx.close();
				}

				remotingServer.shutdown();

				vertxServer.shutdown();

				curatorFramework.close();
				offsetStorage.close();
				FutureUtils.result(streamFactory.closeStreams());
				state.set(State.CLOSED);
				log.info("Close broker service.");
			} else {
				log.warn("Can't close broker service ,current state is :" + state.get());
			}
		} catch (Exception e) {
			log.error("Error when trying to close BrokerService.", e);
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
