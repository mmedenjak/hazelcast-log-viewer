package com.hazelcast.command;

import com.hazelcast.Coordinator;
import com.hazelcast.lineTransformer.JitterLogLineTransformer;
import com.hazelcast.lineTransformer.LogLineTransformer;
import com.hazelcast.lineTransformer.MarkLogLineTransformer;
import com.hazelcast.lineTransformer.ShowFileLogLineTransformer;

public class ShowCommand implements Command {
    private final LogLineTransformer transformer;

    public ShowCommand(String args) {
        this.transformer = parseTransformer(args);
    }

    private LogLineTransformer parseTransformer(String args) {
        final int spaceIdx = args.indexOf(" ");
        final String cmd = spaceIdx > 0 ? args.substring(0, spaceIdx) : args;
        final String rest = spaceIdx > 0 ? args.substring(spaceIdx + 1) : "";
        switch (cmd) {
            case "file":
                return new ShowFileLogLineTransformer();
            case "mark":
                return new MarkLogLineTransformer(rest);
            case "jitter":
                return new JitterLogLineTransformer();
            default:
                throw new IllegalArgumentException("Unknown show command");
        }
    }

    @Override
    public void execute(Coordinator coordinator) {
        coordinator.logLineTransformers.add(transformer);
    }
}
