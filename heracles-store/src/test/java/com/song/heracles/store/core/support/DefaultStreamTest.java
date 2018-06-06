package com.song.heracles.store.core.support;

import com.song.heracles.common.concurrent.OrderedExecutor;
import com.song.heracles.common.constants.ErrorCode;
import com.song.heracles.store.core.Stream;
import com.song.heracles.store.core.StreamFactory;

import org.apache.distributedlog.DistributedLogConfiguration;
import org.apache.distributedlog.DistributedLogConstants;
import org.apache.distributedlog.api.AsyncLogWriter;
import org.apache.distributedlog.api.DistributedLogManager;
import org.apache.distributedlog.api.namespace.Namespace;
import org.apache.distributedlog.api.namespace.NamespaceBuilder;
import org.apache.distributedlog.common.concurrent.FutureUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * @author song
 */
public class DefaultStreamTest {

	private Stream stream;

	@Before
	public void setUp() throws Exception {
		DistributedLogManager dlm = null;
		Namespace namespace = null;
		AsyncLogWriter asyncLogWriter = null;
		try {//my_namespace
			URI uri = URI.create("distributedlog://127.0.0.1:7000/messaging/my_namespace");
			DistributedLogConfiguration conf = new DistributedLogConfiguration();
			conf.setImmediateFlushEnabled(true);
			conf.setOutputBufferSize(0);
			conf.setPeriodicFlushFrequencyMilliSeconds(0);
			conf.setLockTimeout(DistributedLogConstants.LOCK_IMMEDIATE);

			namespace = NamespaceBuilder.newBuilder()
				.conf(conf)
				.uri(uri)
				.regionId(DistributedLogConstants.LOCAL_REGION_ID)
				.clientId("console-writer")
				.build();

			OrderedExecutor orderedExecutor = OrderedExecutor
				.newBuilder()
				.name("heracles-ordered-thread-pool")
				.numThreads(20)
				.maxTasksInQueue(1024)
				.build();
			dlm = namespace.openLog("basic-stream-1");
			stream = new DefaultStream("basic-stream-1", null, conf, namespace, UUID.randomUUID().toString(), orderedExecutor, new StreamFactory() {

				@Override
				public Stream getStream(String name) {
					return null;
				}

				@Override
				public Stream getOrOpenStream(String name) {
					return null;
				}

				@Override
				public CompletableFuture<Void> asyncCreateStream(String name) {
					return null;
				}

				@Override
				public CompletableFuture<List<Void>> closeStreams() {
					return null;
				}
			});
			CountDownLatch latch = new CountDownLatch(1);
			stream.start().thenAccept(aVoid -> {
				System.out.println("Stream has started.");
				latch.countDown();
			}).exceptionally(throwable -> {
				throwable.printStackTrace();
				latch.countDown();
				return null;
			});
			latch.await();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void submitOp() throws InterruptedException {
		WriteOp streamOp = new WriteOp("basic-stream-1", ByteBuffer.wrap("Hello World".getBytes()));
		stream.submitOp(streamOp);
		CompletableFuture<WriteResponse> resultAsync = streamOp.getResultAsync();
		CountDownLatch latch = new CountDownLatch(1);
		resultAsync.thenAccept(response -> {
			Assert.assertEquals(ErrorCode.OK, response.getCode());
			if (response.getCode() == ErrorCode.OK) {
				System.out.println(response.getDlsn().toString());
			} else {
				System.out.println(response.getCode() + "->" + response.getMessage());
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
		FutureUtils.result(stream.closeAsync(), throwable -> {
			throwable.printStackTrace();
			return null;
		});
	}
}