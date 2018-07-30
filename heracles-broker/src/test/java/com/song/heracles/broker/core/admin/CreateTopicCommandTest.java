package com.song.heracles.broker.core.admin;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class CreateTopicCommandTest {

    @Test
    public void createTopic() throws Exception {
        CreateTopicCommand command = new CreateTopicCommand();
        HeraclesAdminTool.main(new String[]{command.getClass().getName(), "-d",
            "distributedlog://127.0.0.1:7000/messaging/my_namespace", "-t", "cmpp-request-1", "-p",
            "6"});
    }
}