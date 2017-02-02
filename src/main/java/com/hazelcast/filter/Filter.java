package com.hazelcast.filter;

import com.hazelcast.LogLine;

public interface Filter {
    boolean filter(LogLine line);
}
