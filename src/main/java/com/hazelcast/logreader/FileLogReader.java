package com.hazelcast.logreader;

import com.hazelcast.LogLine;
import com.hazelcast.LogLineFormat;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public class FileLogReader implements LogReader {
    private final String fileTag;
    private final RandomDirectionBufferedFileReader reader;
    private final Collection<LogLineFormat> logLineFormats;
    private LogLine currentLine;
    private LogLine nextLine;
    private int timeShift;

    public FileLogReader(String file, Collection<LogLineFormat> logLineFormats, String fileTag) {
        try {
            this.fileTag = fileTag;
            this.reader = new RandomDirectionBufferedFileReader(new File(file));
            this.logLineFormats = logLineFormats;
            advance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LogLine currentLine() {
        return currentLine;
    }

    @Override
    public void advance() {
        try {
            while (true) {
                final String lineRead = reader.readLine();
                final Optional<LogLineFormat> lineFormat =
                        logLineFormats.stream().filter(format -> format.matches(lineRead)).findFirst();

                if (lineRead == null) {
                    if (nextLine != null) {
                        nextLine.build();
                    }
                    currentLine = nextLine;
                    nextLine = null;
                    break;
                } else if (lineFormat.isPresent()) {
                    if (nextLine != null) {
                        nextLine.build();
                        currentLine = nextLine;
                        nextLine = new LogLine(fileTag, lineFormat.get(), timeShift);
                        nextLine.lineBuilder.append(lineRead);
                        break;
                    } else {
                        nextLine = new LogLine(fileTag, lineFormat.get(), timeShift);
                        nextLine.lineBuilder.append(lineRead);
                    }
                } else {
                    if (nextLine == null) {
                        throw new RuntimeException("Line was read but it did not match any pattern:\n" + lineRead);
                    }
                    nextLine.lineBuilder.append('\n').append(lineRead);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void jump(DirectionType directionType) {
        try {
            reader.jumpToEnd(directionType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setDirection(DirectionType directionType) {
        if (reader.getDirection() != directionType) {
            reader.setDirection(directionType);
            currentLine = nextLine = null;
            advance();
        }
    }

    @Override
    public void clearTimeShift() {
        timeShift = 0;
    }

    @Override
    public String getFileTag() {
        return fileTag;
    }

    @Override
    public void setTimeShift(int timeShift) {
        this.timeShift = timeShift;
    }
}
