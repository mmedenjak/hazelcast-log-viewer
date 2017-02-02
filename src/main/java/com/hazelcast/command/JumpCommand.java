package com.hazelcast.command;

import com.hazelcast.Coordinator;
import com.hazelcast.logreader.DirectionType;

import static com.hazelcast.logreader.DirectionType.DOWN;
import static com.hazelcast.logreader.DirectionType.UP;

public class JumpCommand extends PgShiftCommand {
    public JumpCommand(DirectionType type) {
        super(type);
    }

    @Override
    public void execute(Coordinator coordinator) {
        coordinator.fileReaders.forEach(r -> {
            r.jump(directionType);
            r.setDirection(directionType == UP ? DOWN : UP);
        });
        printPage(coordinator);
    }

    public static void printHelp() {
        System.out.println("q - home");
        System.out.println("a - end");
    }
}
