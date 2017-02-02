package com.hazelcast.command;

import com.hazelcast.Coordinator;
import com.hazelcast.filter.FileFilter;
import com.hazelcast.filter.Filter;
import com.hazelcast.filter.LineFilter;
import com.hazelcast.filter.NotFilter;
import com.hazelcast.filter.TokenFilter;

public class FilterCommand implements Command {
    private final Filter filter;

    public FilterCommand(String args) {
        this.filter = parseFilter(args);
    }

    private Filter parseFilter(String args) {
        final int spaceIdx = args.indexOf(" ");
        final String cmd = args.substring(0, spaceIdx);
        final String rest = args.substring(spaceIdx + 1);
        switch (cmd) {
            case "not":
                return new NotFilter(parseFilter(rest));
            case "file":
                return new FileFilter(rest);
            case "line":
                return new LineFilter(rest);
            case "token":
                return new TokenFilter(rest);
            default:
                throw new IllegalArgumentException("Unknown filter command");
        }
    }

    @Override
    public void execute(Coordinator coordinator) {
        coordinator.filters.add(filter);
    }
}
