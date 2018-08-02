package com.song.heracles.broker.core.admin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateTopicCommandTest {

    public static void main(String[] args) {
        CreateTopicCommand command = new CreateTopicCommand();
        HeraclesAdminTool.main(new String[]{command.getClass().getName(), "-d",
            "distributedlog://127.0.0.1:7000/songsong/my_namespace", "-t", "cmpp-request-1", "-p",
            "6","-z","127.0.0.1:7000"});
    }
}