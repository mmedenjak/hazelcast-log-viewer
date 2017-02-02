package com.hazelcast.command;

import com.hazelcast.Coordinator;
import com.hazelcast.logreader.LogReader;

import java.util.Optional;

public class TimeShiftCommand implements Command {
    private final Integer timeShift;
    private final String fileId;

    public TimeShiftCommand(String args) {
        final int spaceIdx = args.indexOf(" ");
        this.fileId = args.substring(0, spaceIdx);
        this.timeShift = Integer.valueOf(args.substring(spaceIdx + 1));
    }

    @Override
    public void execute(Coordinator coordinator) {
        final Optional<LogReader> reader =
                coordinator.fileReaders.stream().filter(r -> r.getFileTag().equals(fileId)).findFirst();
        if (reader.isPresent()) {
            reader.get().setTimeShift(timeShift);
        } else {
            throw new IllegalArgumentException("Could not find file with id " + fileId);
        }
    }

    public static void printHelp() {
        System.out.println("time-shift {file tag} {ms} - shift logs for {file id} for {ms}");
    }
}
