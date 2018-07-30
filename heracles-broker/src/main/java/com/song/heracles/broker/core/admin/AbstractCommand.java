package com.song.heracles.broker.core.admin;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public abstract class AbstractCommand implements Command {


    @Override
    public int runCmd(String[] args) throws Exception {
        if (args.length <= 0) {
            printUsage();
            return -1;
        }
        try {
            DefaultParser parser = new DefaultParser();
            CommandLine cmdline = parser.parse(this.getOptions(), args);
            return this.runCmd(cmdline);
        } catch (ParseException var4) {
            this.printUsage();
            return -1;
        }
    }

    @Override
    public void printUsage() {
        HelpFormatter helpFormatter = new HelpFormatter();
        System.out.println(getName() + ": " + this.getDescription());
        helpFormatter.printHelp(getName() + " [options]", this.getOptions());
    }

    abstract Options getOptions();

    abstract int runCmd(CommandLine cmdline) throws Exception;
}
