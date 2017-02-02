package com.hazelcast.lineTransformer;

import com.hazelcast.LogLine;

import static org.fusesource.jansi.Ansi.ansi;

public class ShowFileLogLineTransformer implements LogLineTransformer {

    @Override
    public LogLine transform(LogLine line) {
        line.line = ansi().bgBrightGreen().a("F:").a(line.fileTag).reset() + " " + line.line;
        return line;
    }

    public static void printHelp() {
        System.out.println("show file - show file id's");
    }
}
