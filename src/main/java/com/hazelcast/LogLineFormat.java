package com.hazelcast;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class LogLineFormat {
    public final Pattern pattern;
    public final int timeToken;
    public final SimpleDateFormat timeFormat;

    public LogLineFormat(Pattern pattern, int timeToken, SimpleDateFormat timeFormat) {
        this.pattern = pattern;
        this.timeToken = timeToken;
        this.timeFormat = timeFormat;
    }

    public boolean matches(String lineRead) {
        return lineRead != null && pattern.matcher(lineRead).matches();
    }
}
