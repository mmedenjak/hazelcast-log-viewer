package com.hazelcast.filter;

import com.hazelcast.LogLine;

public class NotFilter implements Filter {
    private final Filter wrapped;

    public NotFilter(Filter wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public boolean filter(LogLine line) {
        return !wrapped.filter(line);
    }

    public static void printHelp() {
        System.out.println("filter not * - same as above but with not");
    }
}
