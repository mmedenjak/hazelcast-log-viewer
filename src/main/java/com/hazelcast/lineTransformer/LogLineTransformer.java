package com.hazelcast.lineTransformer;

import com.hazelcast.LogLine;

public interface LogLineTransformer {
    LogLine transform(LogLine line);
}
