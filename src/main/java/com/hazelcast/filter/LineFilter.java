package com.hazelcast.filter;

import com.hazelcast.LogLine;

import java.util.regex.Pattern;

public class LineFilter implements Filter {
    private final Pattern pattern;

    public LineFilter(String pattern) {
        this.pattern = Pattern.compile(pattern, Pattern.DOTALL);
    }

    @Override
    public boolean filter(LogLine line) {
        return pattern.matcher(line.line).matches();
    }

    public static void printHelp() {
        System.out.println("filter line {regex} - show lines which match {regex}");
    }
}
