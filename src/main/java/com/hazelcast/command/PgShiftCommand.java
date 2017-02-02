package com.hazelcast.command;

import com.hazelcast.Coordinator;
import com.hazelcast.LogLine;
import com.hazelcast.lineTransformer.LogLineTransformer;
import com.hazelcast.logreader.DirectionType;
import com.hazelcast.logreader.LogReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.fusesource.jansi.Ansi.ansi;

public class PgShiftCommand implements Command {
    protected final DirectionType directionType;

    public PgShiftCommand(DirectionType type) {
        this.directionType = type;
    }

    @Override
    public void execute(Coordinator coordinator) {
        coordinator.fileReaders.forEach(r -> r.setDirection(directionType));
        printPage(coordinator);
    }

    protected void printPage(Coordinator coordinator) {
        System.out.println(ansi().eraseScreen());
        printPage(coordinator, null);
    }

    protected void printPage(Coordinator coordinator, LogLine first) {
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println();
        final List<String> lines = new ArrayList<>();
        if (first != null) {
            lines.add(transform(coordinator, first));
        }
        int pageSize = 0;
        while (pageSize < coordinator.getPageSize()) {
            final String line = transform(coordinator, nextLine(coordinator));
            if (line == null) break;
            lines.add(line);
            pageSize += line.length() - line.replace("\n", "").length() + 1;
        }
//        lines.addAll(Stream.generate(() -> )
//                           .limit(coordinator.getPageSize())
//                           .filter(Objects::nonNull)
//                           .collect(Collectors.toList()));
        if (directionType == DirectionType.UP) {
            Collections.reverse(lines);
        }
        lines.forEach(System.out::println);
    }

    protected String transform(Coordinator coordinator, LogLine nextLine) {
        if (nextLine == null) {
            return null;
        }
        for (LogLineTransformer transformer : coordinator.logLineTransformers) {
            nextLine = transformer.transform(nextLine);
        }
        return nextLine.toString();
    }

    protected LogLine nextLine(Coordinator coordinator) {
        // TODO handle end-of-file
        while (true) {
            final LogReader nextReader = coordinator.fileReaders.stream()
                                                                .min(getComparator())
                                                                .orElse(null);
            final LogLine line = nextReader.currentLine();
            if (line == null) {
                return null;
            }
            nextReader.advance();
            if (coordinator.filters.stream().allMatch(f -> f.filter(line))) {
                return line;
            }
        }
    }

    private Comparator<LogReader> getComparator() {
        final Comparator<LogReader> comp = Comparator.comparing(this::getLogLineTime);
        return directionType == DirectionType.DOWN ? comp : comp.reversed();
    }

    private long getLogLineTime(LogReader reader) {
        final LogLine l = reader.currentLine();
        return l != null ? l.time : Long.MIN_VALUE;
    }

    public static void printHelp() {
        System.out.println("w - page up");
        System.out.println("s - page down");
    }
}
