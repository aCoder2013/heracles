package com.song.heracles.broker.core.admin;

import org.apache.bookkeeper.util.ReflectionUtils;
import org.apache.distributedlog.tools.Tool;

public class DlogTool {

    public static int process(String[] args) {
        int rc = -1;
        if (args.length <= 0) {
            System.err.println("No tool to run.");
            System.err.println("");
            System.err.println("Usage : Tool <tool_class_name> <options>");
            return rc;
        }
        String toolClass = args[0];
        try {
            Tool tool = ReflectionUtils.newInstance(toolClass, Tool.class);
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
            rc = tool.run(newArgs);
        } catch (Throwable t) {
            System.err.println("Fail to run tool " + toolClass + " : ");
            t.printStackTrace();
        }
        return rc;
    }
}
