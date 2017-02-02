
package com.hazelcast.command;

import com.hazelcast.Coordinator;

public class ClearCommand implements Command {

    private final Type type;

    public ClearCommand(String args) {
        switch (args) {
            case "filter":
                this.type = Type.FILTER;
                break;
            case "show":
                this.type = Type.SHOW;
                break;
            case "time-shift":
                this.type = Type.TIME_SHIFT;
                break;
            default:
                throw new IllegalArgumentException("Unknown clear command");
        }
    }

    @Override
    public void execute(Coordinator coordinator) {
        switch (type) {
            case FILTER:
                coordinator.filters.clear();
                break;
            case SHOW:
                coordinator.logLineTransformers.clear();
                break;
            case TIME_SHIFT:
                coordinator.fileReaders.forEach(reader -> reader.clearTimeShift());
                break;
        }
    }

    public static void printHelp() {
        System.out.println("clear filter - clear filter");
        System.out.println("clear show - clear transformers");
        System.out.println("clear time-shift - clear time-shift");
    }

    private enum Type {
        FILTER, SHOW, TIME_SHIFT;
    }
}
