package com.song.heracles.broker.core.admin;

import org.apache.bookkeeper.util.ReflectionUtils;

public class HeraclesAdminTool {

    public static void main(String[] args) {
        if (args.length <= 0) {
            System.err.println("No command to run.");
            System.err.println("");
            System.err.println("Usage : command <command_class_name> <options>");
            System.exit(-1);
        } else {
            int rc = -1;
            String commandClass = args[0];
            Command command = ReflectionUtils.newInstance(commandClass, Command.class);
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
            try {
                rc = command.runCmd(newArgs);
            } catch (Exception e) {
                System.err.println("Fail to run command : " + commandClass + ":");
                e.printStackTrace();
            }
            System.exit(rc);
        }
    }

}
