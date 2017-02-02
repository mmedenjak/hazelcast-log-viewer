package com.hazelcast.command;

import com.hazelcast.Coordinator;

public class QuitCommand implements Command {
    @Override
    public void execute(Coordinator coordinator) {
    }

    public static void printHelp() {
        System.out.println("quit - quit");
    }
}
