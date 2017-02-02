package com.hazelcast.command;

import com.hazelcast.Coordinator;
import com.hazelcast.LogLine;
import com.hazelcast.ProgressIndicator;
import com.hazelcast.logreader.DirectionType;

import java.util.regex.Pattern;

public class SearchCommand extends PgShiftCommand {

    public SearchCommand(DirectionType type) {
        super(type);
    }

    @Override
    public void execute(Coordinator coordinator) {
        final Pattern p = coordinator.getSearchPattern();
        if (p == null) {
            return;
        }
        LogLine l;
        final ProgressIndicator indicator = new ProgressIndicator();
        indicator.start();
        while (!matches((l = nextLine(coordinator)), p)) {

        }
        indicator.stop();
        printPage(coordinator, l);
    }

    private boolean matches(LogLine line, Pattern p) {
        return line == null || p.matcher(line.line).matches();
    }

    public static void printHelp() {
        System.out.println("e - next match up");
        System.out.println("d - next match down");
    }
}
