package com.hazelcast.filter;

import com.hazelcast.LogLine;

public class FileFilter implements Filter {
    private final String fileTag;

    public FileFilter(String args) {
        this.fileTag = args;
    }

    @Override
    public boolean filter(LogLine line) {
        return line.fileTag.equals(fileTag);
    }

    public static void printHelp() {
        System.out.println("filter file {id} - show lines for file with id {id}");
    }
}
