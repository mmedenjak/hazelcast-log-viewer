package com.hazelcast.command;

import com.hazelcast.Coordinator;

public interface Command {
    void execute(Coordinator coordinator);
}
