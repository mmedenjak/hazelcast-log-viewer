package com.hazelcast.lineTransformer;

import com.hazelcast.LogLine;

import static org.fusesource.jansi.Ansi.ansi;

public class JitterLogLineTransformer implements LogLineTransformer {

    private long previousTime = Long.MIN_VALUE;

    @Override
    public LogLine transform(LogLine line) {
        final long diff = previousTime != Long.MIN_VALUE ? line.time - previousTime : 0;
        previousTime = line.time;
        line.line = ansi().bgBrightGreen().a("J:").a(diff).reset() + " " + line.line;
        return line;
    }

    public static void printHelp() {
        System.out.println("show jitter - show diff between log lines");
    }
}
