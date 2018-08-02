package com.song.heracles.broker.core.admin;

import com.song.heracles.broker.core.zk.AdminZkClient;
import com.song.heracles.common.util.TopicUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Command that's used to create heracles topic
 */
public class CreateTopicCommand extends AbstractCommand {

    private final Options options = new Options();

    public CreateTopicCommand() {
        this.options.addOption("d", "distributed-log-uri", true, "distributed-log uri");
        this.options.addOption("t", "topic", true, "topic name");
        this.options.addOption("p", "partitions", true, "topic partition number,default is 16.");
        this.options.addOption("z", "heraclesZkServers", true,
            "list of zk servers that heracles uses,comma separated host:port pairs, each corresponding to a zk server");
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Create a heracles topic";
    }

    @Override
    Options getOptions() {
        return options;
    }

    @Override
    int runCmd(CommandLine cmdline) throws Exception {
        boolean topicOption = cmdline.hasOption("t");
        if (!topicOption) {
            System.err.println("No topic name!");
            return -1;
        }
        if (!cmdline.hasOption("z")) {
            System.err.println("No zkServers!");
            return -1;
        }
        String zkServers = cmdline.getOptionValue("z");

        String topic = cmdline.getOptionValue("t");

        TopicUtils.validate(topic);

        if (TopicUtils.hasCollisionChars(topic)) {
            System.err.println(
                "WARNING: Due to limitations in metric names, topics with a period ('.') or underscore ('_') could collide. To avoid issues it is best to use either, but not both.");
        }

        int partitions = Integer.parseInt(cmdline.getOptionValue("p", "16"));

        CuratorFramework curatorFramework = CuratorFrameworkFactory
            .newClient(zkServers, 6000, 6000, new ExponentialBackoffRetry(1000, 3, 5000));
        curatorFramework.start();
        AdminZkClient adminZkClient = new AdminZkClient(curatorFramework);
        adminZkClient.createTopicPartitionsConfigInZK(topic, partitions);
        System.out.println("Write topic metadata to zookeeper.");
        String dlogUri = cmdline.getOptionValue("d");
        return DlogTool.process(new String[]{"org.apache.distributedlog.tools.DistributedLogTool", "create", "-u",
            dlogUri, "-f", "true", "-r", topic, "-e", "1-" + partitions});
    }
}
