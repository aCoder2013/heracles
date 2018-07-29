package com.song.heracles.broker.core.support;

import com.google.common.base.Charsets;
import com.song.heracles.broker.core.Offset;
import com.song.heracles.broker.core.OffsetStorage;
import com.song.heracles.broker.core.PartitionedTopic;
import com.song.heracles.common.util.GsonUtils;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.distributedlog.DLSN;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

/**
 * @author song
 */
@Slf4j
public class ZkOffsetStorage implements OffsetStorage {

    private static final String ZK_OFFSET_STORAGE_PREFIX = "/heracles/offset";

    private static final String ZK_OFFSET_STORAGE_PREFIX_PATH = ZK_OFFSET_STORAGE_PREFIX + "/";

    private final CuratorFramework curatorFramework;

    private final ConcurrentMap<PartitionedTopic, Offset> offsetCache = new ConcurrentHashMap<>();

    public ZkOffsetStorage(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    @Override
    public void start() throws Exception {
        Stat stat = this.curatorFramework.checkExists()
            .forPath(ZK_OFFSET_STORAGE_PREFIX);
        if (stat == null) {
            this.curatorFramework.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath(ZK_OFFSET_STORAGE_PREFIX, new byte[0]);
        }
    }

    @Override
    public Offset readOffsetFromCache(PartitionedTopic partitionedTopic) throws Exception {
        Offset offset = this.offsetCache.get(partitionedTopic);
        if (offset == null) {
            synchronized (this) {
                String path = buildPath(partitionedTopic);
                try {
                    offset = this.offsetCache.get(partitionedTopic);
                    if (offset == null) {
                        byte[] bytes = this.curatorFramework.getData().forPath(path);
                        offset = GsonUtils.fromJson(new String(bytes), Offset.class);
                        this.offsetCache.put(partitionedTopic, offset);
                        return offset;
                    }
                } catch (KeeperException.NoNodeException e) {
                    offset = new Offset(DLSN.InitialDLSN);
                    this.offsetCache.put(partitionedTopic, offset);
                    String json = GsonUtils.toJson(offset);
                    if (StringUtils.isBlank(json)) {
                        throw new IllegalArgumentException(
                            "Can't convert Offset to JSON format :" + offset);
                    }
                    this.curatorFramework.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(path, json.getBytes(Charsets.UTF_8));
                }
            }
        }
        return offset;
    }

    @Override
    public Offset readOffsetFromStorage(PartitionedTopic partitionedTopic) throws Exception {
        String path = buildPath(partitionedTopic);
        try {
            byte[] bytes = this.curatorFramework.getData().forPath(path);
            return GsonUtils.fromJson(new String(bytes), Offset.class);
        } catch (KeeperException.NoNodeException e) {
            Offset offset = new Offset(DLSN.InitialDLSN);
            String json = GsonUtils.toJson(offset);
            if (StringUtils.isBlank(json)) {
                throw new IllegalArgumentException(
                    "Can't convert Offset to JSON format :" + offset);
            }
            this.curatorFramework.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath(path, json.getBytes(Charsets.UTF_8));
            return offset;
        }
    }

    @Override
    public void updateOffset(PartitionedTopic partitionedTopic, Offset offset) {
        this.offsetCache.put(partitionedTopic, offset);
    }

    @Override
    public void persistOffset(PartitionedTopic partitionedTopic) throws Exception {
        synchronized (this) {
            if (this.offsetCache.containsKey(partitionedTopic)) {
                Offset offset = this.offsetCache.get(partitionedTopic);
                String path = buildPath(partitionedTopic);
                String json = GsonUtils.toJson(offset);
                if (StringUtils.isBlank(json)) {
                    throw new IllegalArgumentException(
                        "Can't convert Offset to json : " + offset.toString());
                }
                try {
                    this.curatorFramework
                        .setData()
                        .forPath(path, json.getBytes(Charsets.UTF_8));
                } catch (KeeperException.NoNodeException e) {
                    this.curatorFramework.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(path, json.getBytes(Charsets.UTF_8));
                }
                log.info("Successfully persist offset for topic:{},queueId:{}.",
                    partitionedTopic.getTopic(), partitionedTopic.getIndex());
            }
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (this) {
            this.offsetCache.forEach((key, value) -> {
                try {
                    persistOffset(key);
                } catch (KeeperException.NoNodeException e) {
                    String json = GsonUtils.toJson(value);
                    if (StringUtils.isBlank(json)) {
                        log.warn("Can't convert Offset to JSON format :{}-{}", key, value);
                        return;
                    }
                    try {
                        this.curatorFramework.create()
                            .creatingParentsIfNeeded()
                            .withMode(CreateMode.PERSISTENT)
                            .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                            .forPath(buildPath(key), json.getBytes(Charsets.UTF_8));
                    } catch (Exception ex) {
                        log.error("Persist offset failed for :" + key, e);
                    }
                } catch (Exception e) {
                    log.error("Persist offset failed for :" + key, e);
                }
            });
        }
    }

    private String buildPath(PartitionedTopic partitionedTopic) {
        return ZK_OFFSET_STORAGE_PREFIX_PATH + partitionedTopic.getTopic() + "/" + partitionedTopic
            .getIndex();
    }
}
