package com.hazelcast.logreader;

import com.hazelcast.LogLine;

public interface LogReader {
    LogLine currentLine();

    void advance();

    void close();

    void jump(DirectionType directionType);

    void setDirection(DirectionType directionType);

    void clearTimeShift();

    String getFileTag();

    void setTimeShift(int timeShift);
}
