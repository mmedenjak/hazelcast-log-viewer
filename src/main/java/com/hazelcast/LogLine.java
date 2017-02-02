package com.hazelcast;

import java.util.regex.Matcher;

public class LogLine {
    public final LogLineFormat format;
    public final String fileTag;
    private final int timeShift;
    public StringBuilder lineBuilder = new StringBuilder();
    public long time;
    public String line;
    public String[] tokens;

    public LogLine(String fileTag, LogLineFormat format, int timeShift) {
        this.fileTag = fileTag;
        this.format = format;
        this.timeShift = timeShift;
    }

    public void build() {
        this.line = lineBuilder.toString();
        this.lineBuilder = null;
        final Matcher matcher = format.pattern.matcher(line);
        if (!matcher.matches()) {
            throw new RuntimeException("Line does not match pattern " + format.pattern + ":\n" + line);
        }

        tokens = new String[matcher.groupCount()];
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = matcher.group(i + 1);
        }

        try {
            time = format.timeFormat.parse(tokens[format.timeToken]).getTime();
        } catch (Exception e) {
            time = Long.MIN_VALUE;
        }
        time += timeShift;
    }

    @Override
    public String toString() {
        return line;
    }
}
