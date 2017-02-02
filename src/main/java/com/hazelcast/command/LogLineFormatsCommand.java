package com.hazelcast.command;

import com.hazelcast.Coordinator;
import com.hazelcast.LogLine;
import com.hazelcast.logreader.LogReader;

import java.util.Objects;

public class LogLineFormatsCommand implements Command {

    @Override
    public void execute(Coordinator coordinator) {
        final LogLine logLine = coordinator.fileReaders.stream()
                                                       .map(LogReader::currentLine)
                                                       .filter(Objects::nonNull)
                                                       .findFirst()
                                                       .get();
        System.out.println("Line pattern " + logLine.format.pattern);
        System.out.println("Time token " + logLine.format.timeToken);
        System.out.println("Time format " + logLine.format.timeFormat.toPattern());
        System.out.println("Tokens example:");
        System.out.println(logLine.line);
        final String[] tokens = logLine.tokens;
        for (int i = 0; i < tokens.length; i++) {
            System.out.println(i + " : " + tokens[i]);
        }
    }

    public static void printHelp() {
        System.out.println("line-format - show line formats");
    }
}
