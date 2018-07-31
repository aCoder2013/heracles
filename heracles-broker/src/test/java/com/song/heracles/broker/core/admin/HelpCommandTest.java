package com.song.heracles.broker.core.admin;

import org.junit.Test;

public class HelpCommandTest {

    @Test
    public void printUsage() {
        HelpCommand command = new HelpCommand();
        HeraclesAdminTool.main(new String[]{command.getClass().getName(),"create"});
    }
}