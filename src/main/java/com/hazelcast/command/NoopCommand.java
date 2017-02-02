package com.hazelcast.command;

import com.hazelcast.Coordinator;

public class NoopCommand implements Command {
    @Override
    public void execute(Coordinator coordinator) {
    }
}
