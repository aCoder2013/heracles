package com.song.heracles.broker.core.admin;

public class CreateNamespaceCommandTest {

    public static void main(String[] args) {
        CreateNamespaceCommand command = new CreateNamespaceCommand();
        HeraclesAdminTool.main(new String[]{command.getClass().getName(), "-d",
            "distributedlog://127.0.0.1:7000/songsong/my_namespace", "-s",
            "127.0.0.1:7000"});
    }
}