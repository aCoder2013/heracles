package com.song.heracles.broker.config;

import com.song.heracles.common.util.Required;
import lombok.Getter;
import lombok.Setter;

/**
 * @author song
 */
@Getter
@Setter
public class BrokerConfiguration {

    /**
     * identify each broker,must be unique
     */
    private String clientId = "default";

    @Required
    private String clusterName;

    @Required
    private String zkServers;

    /**
     * Zookeeper session timeout in milliseconds
     */
    private int zooKeeperSessionTimeoutMillis = 60000;

    /**
     * Zookeeper connect timeout in milliseconds
     */
    private int zookeeperConnectionTimeout = 15000;

    private int brokerPort = 7160;

    // Hostname or IP address the service binds on.
    private String bindAddress = "0.0.0.0";

    @Required
    private String distributedLogUri;
}
