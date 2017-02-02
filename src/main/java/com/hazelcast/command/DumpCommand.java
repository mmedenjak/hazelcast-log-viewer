package com.hazelcast.command;

import com.hazelcast.Coordinator;
import com.hazelcast.LogLine;
import com.hazelcast.ProgressIndicator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.hazelcast.logreader.DirectionType.DOWN;
import static com.hazelcast.logreader.DirectionType.UP;

public class DumpCommand extends PgShiftCommand implements Command {
    private final String fileOut;

    public DumpCommand(String fileOut) {
        super(null);
        this.fileOut = fileOut;
    }

    @Override
    public void execute(Coordinator coordinator) {
        final File file = new File(fileOut);
        try (final BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            coordinator.fileReaders.forEach(r -> {
                r.jump(UP);
                r.setDirection(DOWN);
            });
            LogLine l;
            final ProgressIndicator indicator = new ProgressIndicator();
            indicator.start();
            while ((l = nextLine(coordinator)) != null) {
                w.write(transform(coordinator, l) + "\n");
            }
            indicator.stop();
            System.out.println("Dump done to " + file.getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printHelp() {
        System.out.println("dump {file name} - dump logs with current setup to {file name}");
    }
}
