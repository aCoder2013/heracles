package com.song.heracles.broker.core.zk;

import com.song.heracles.common.util.GsonUtils;
import java.util.Objects;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.ZooDefs;

public class AdminZkClient {

    public static final String SEPARATOR = "/";

    public static final String HERACLES_ZK_NODE_PATH = "/heracles";

    public static final String BROKER_ZK_NODE_PATH = HERACLES_ZK_NODE_PATH + "/brokers";

    public static final String TOPIC_ZK_NODE_PATH = BROKER_ZK_NODE_PATH + "/topics";

    public static final String TOPIC_PARTITIONS_ZK_NODE_PATH = TOPIC_ZK_NODE_PATH + "/partitions";

    public static final String TOPIC_PARTITIONS_CONFIG_ZK_NODE_PATH =
        TOPIC_ZK_NODE_PATH + "/config";

    public static final String TOPIC_PARTITIONS_STATE_ZK_NODE_PATH =
        TOPIC_PARTITIONS_ZK_NODE_PATH + "/state";

    private final CuratorFramework curatorFramework;

    public AdminZkClient(CuratorFramework curatorFramework) {
        Objects.requireNonNull(curatorFramework);
        this.curatorFramework = curatorFramework;
    }

    public static String topicPath(String path) {
        return TOPIC_ZK_NODE_PATH + path;
    }

    public static String topicPartitionsPath(String path) {
        return TOPIC_PARTITIONS_ZK_NODE_PATH + path;
    }

    public static String topicPartitionsConfigPath(String path) {
        return TOPIC_PARTITIONS_CONFIG_ZK_NODE_PATH + path;
    }

    public static String topicPartitionsStatePath(String path) {
        return TOPIC_PARTITIONS_STATE_ZK_NODE_PATH + path;
    }

    public void createTopicPartitionsConfigInZK(String topic, int partitions) throws Exception {
        try {
            this.curatorFramework.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath(TOPIC_PARTITIONS_CONFIG_ZK_NODE_PATH,
                    GsonUtils.toJson(new TopicConfig(topic, partitions)).getBytes());
        } catch (NodeExistsException ignore) {
            //maybe throw it ?
        }
    }

}
