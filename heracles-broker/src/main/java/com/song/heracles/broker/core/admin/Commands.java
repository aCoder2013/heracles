package com.song.heracles.broker.core.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Commands {

    private static final Map<String, Command> COMMANDS = new HashMap<>();

    static {
//        addCommand(new HelpCommand());
        addCommand(new CreateTopicCommand());
        addCommand(new CreateNamespaceCommand());
    }

    private static void addCommand(Command command) {
        COMMANDS.put(command.getName(), command);
    }

    public static List<Command> getAllCommand() {
        return Collections.unmodifiableList(new ArrayList<>(COMMANDS.values()));
    }

    public static Map<String,Command> getCommands(){
        return Collections.unmodifiableMap(COMMANDS);
    }
}
