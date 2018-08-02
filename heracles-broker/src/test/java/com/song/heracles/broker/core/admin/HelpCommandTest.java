package com.song.heracles.broker.core.admin;

public class HelpCommandTest {


    public static void main(String[] args) {
        HelpCommand command = new HelpCommand();
        HeraclesAdminTool.main(new String[]{command.getClass().getName(),"create"});
    }
}