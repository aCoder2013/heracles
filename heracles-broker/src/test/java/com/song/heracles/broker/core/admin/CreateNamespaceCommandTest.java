package com.song.heracles.broker.core.admin;

import org.junit.Test;

public class CreateNamespaceCommandTest {

    @Test
    public void runCmd() throws Exception {
        CreateNamespaceCommand command = new CreateNamespaceCommand();
        HeraclesAdminTool.main(new String[]{command.getClass().getName(), "-d",
            "distributedlog://127.0.0.1:7000/songsong/my_namespace", "-s",
            "127.0.0.1:7000"});
    }
}