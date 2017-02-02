package com.hazelcast;

import com.hazelcast.command.Command;
import com.hazelcast.command.QuitCommand;
import com.hazelcast.filter.Filter;
import com.hazelcast.lineTransformer.LogLineTransformer;
import com.hazelcast.logreader.FileLogReader;
import com.hazelcast.logreader.LogReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

public class Coordinator {
    public final ArrayList<LogReader> fileReaders;
    public final ArrayList<Filter> filters = new ArrayList<>();
    public final ArrayList<LogLineTransformer> logLineTransformers = new ArrayList<>();
    public Collection<LogLineFormat> logLineFormats = Arrays.asList(
            new LogLineFormat(Util.TEST_PATTERN, Util.TEST_TIME_TOKEN, Util.TEST_DATE_FORMAT),
            new LogLineFormat(Util.CLUSTER_PATTERN, Util.CLUSTER_TIME_TOKEN, Util.CLUSTER_DATE_FORMAT),
            new LogLineFormat(Pattern.compile("Started Running Test.*"), -1, null),
            new LogLineFormat(Pattern.compile("Finished Running Test.*"), -1, null)
    );

    private final CommandReader commandReader;
    private Pattern searchPattern;
    private int pageSize = Util.DEFAULT_PAGE_SIZE;

    public Coordinator(String[] args) {
        fileReaders = new ArrayList<>(args.length);
        for (int i = 0; i < args.length; i++) {
            fileReaders.add(new FileLogReader(args[i], logLineFormats, String.valueOf(i)));
        }
        commandReader = new CommandReader();
    }

    public void setSearchPattern(Pattern searchPattern) {
        this.searchPattern = searchPattern;
    }

    public Pattern getSearchPattern() {
        return searchPattern;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void start() {
        try {
            while (true) {
                final Command cmd = commandReader.next();
                if (cmd instanceof QuitCommand) {
                    break;
                } else {
                    cmd.execute(this);
                }
            }

        } finally {
            commandReader.close();
            fileReaders.forEach(LogReader::close);
        }
    }
}
