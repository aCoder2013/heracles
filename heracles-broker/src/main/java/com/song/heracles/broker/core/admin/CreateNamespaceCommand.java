package com.song.heracles.broker.core.admin;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.distributedlog.tools.Tool;

/**
 * Command used to create distributed log
 */
public class CreateNamespaceCommand extends AbstractCommand {

    private final Options options = new Options();

    public CreateNamespaceCommand() {
        this.options.addOption("d", "distributed-log-uri", true, "distributed-log uri");
        this.options.addOption("s", "bkZkServers", true,
            "ZooKeeper servers used for bookkeeper for writers.");
    }

    @Override
    Options getOptions() {
        return options;
    }

    @Override
    int runCmd(CommandLine cmdline) throws Exception {
        String dlogUri = cmdline.getOptionValue("d");
        String bkZkServers = cmdline.getOptionValue("s");
        Tool.main(new String[]{"org.apache.distributedlog.admin.DistributedLogAdmin", "bind", "-l",
            "/ledgers", "-s", bkZkServers, "-c", dlogUri});
        return 0;
    }

    @Override
    public String getName() {
        return "create-namespace";
    }

    @Override
    public String getDescription() {
        return "create a distributed log namespace";
    }
}
