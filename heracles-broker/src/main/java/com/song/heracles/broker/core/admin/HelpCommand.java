package com.song.heracles.broker.core.admin;

import java.util.Map;

public class HelpCommand implements Command {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "describe the usage of admin commands";
    }

    @Override
    public int runCmd(String[] args) throws Exception {
        if (args.length == 0) {
            printToolUsage();
            return -1;
        }

        String cmdName = args[0];
        Command command = Commands.getCommands().get(cmdName);
        if (null == command) {
            System.err.println("Unknown command :" + cmdName);
            printToolUsage();
            return -1;
        }
        command.printUsage();
        System.out.println("");
        return 0;
    }

    @Override
    public void printUsage() {
        System.out.println(getName() + ": " + getDescription());
        System.out.println("");
        System.out.println("usage: " + getName() + " <command>");
    }

    private void printToolUsage() {
        System.out.println("Usage: " + getName() + " <command>");
        System.out.println("");
        int maxKeyLength = 0;
        Map<String, Command> commands = Commands.getCommands();
        for (String key : commands.keySet()) {
            if (key.length() > maxKeyLength) {
                maxKeyLength = key.length();
            }
        }
        maxKeyLength += 2;
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            StringBuilder spacesBuilder = new StringBuilder();
            int numSpaces = maxKeyLength - entry.getKey().length();
            for (int i = 0; i < numSpaces; i++) {
                spacesBuilder.append(" ");
            }
            System.out.println(
                "\t" + entry.getKey() + spacesBuilder.toString() + ": " + entry.getValue()
                    .getDescription());
        }
        System.out.println("");
    }
}
