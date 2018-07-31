package com.song.heracles.broker.core.admin;

import com.song.heracles.common.util.TopicUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.apache.distributedlog.tools.Tool;

/**
 * Command that's used to create heracles topic
 */
public class CreateTopicCommand extends AbstractCommand {

    private final Options options = new Options();

    public CreateTopicCommand() {
        this.options.addOption("d", "distributed-log-uri", true, "distributed-log uri");
        this.options.addOption("t", "topic", true, "topic name");
        this.options
            .addOption("p", "partitions", true, "topic partition number,default is 16.");
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
        String topic = cmdline.getOptionValue("t");
        if (StringUtils.isBlank(topic)) {
            System.err.println("Topic name can't be null or empty.");
            return -1;
        }
        if (TopicUtils.hasCollisionChars(topic)) {
            System.err.println(
                "WARNING: Due to limitations in metric names, topics with a period ('.') or underscore ('_') could collide. To avoid issues it is best to use either, but not both.");
        }
        String dlogUri = cmdline.getOptionValue("d");
        int partitions = Integer.parseInt(cmdline.getOptionValue("p", "16"));
        Tool.main(new String[]{"org.apache.distributedlog.tools.DistributedLogTool", "create", "-u",
            dlogUri, "-f", "true", "-r", topic, "-e", "1-" + partitions});
        return 0;
    }
}
