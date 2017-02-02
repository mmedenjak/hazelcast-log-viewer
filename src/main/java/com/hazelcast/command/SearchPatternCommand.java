package com.hazelcast.command;

import com.hazelcast.Coordinator;
import com.hazelcast.lineTransformer.MarkLogLineTransformer;
import com.hazelcast.lineTransformer.MarkLogLineTransformer.Type;
import org.fusesource.jansi.Ansi.Color;

import java.util.regex.Pattern;

public class SearchPatternCommand implements Command {
    private final Pattern searchPattern;

    public SearchPatternCommand(String args) {
        this.searchPattern = Pattern.compile(args, Pattern.DOTALL);
    }

    @Override
    public void execute(Coordinator coordinator) {
        coordinator.setSearchPattern(searchPattern);
        coordinator.logLineTransformers.add(new MarkLogLineTransformer(Type.BG, searchPattern, Color.GREEN));
    }

    public static void printHelp() {
        System.out.println("search {pattern} - change search pattern to {pattern}");
    }
}
