package com.song.heracles.broker.core.admin;

/**
 * Interface for a admin command to run in commandline
 */
public interface Command {

    String getName();

    String getDescription();

    int runCmd(String[] var) throws Exception;

    void printUsage();
}
