package com.hazelcast;

import com.hazelcast.command.ClearCommand;
import com.hazelcast.command.Command;
import com.hazelcast.command.DumpCommand;
import com.hazelcast.command.FilterCommand;
import com.hazelcast.command.JumpCommand;
import com.hazelcast.command.LogLineFormatsCommand;
import com.hazelcast.command.NoopCommand;
import com.hazelcast.command.PageSizeCommand;
import com.hazelcast.command.PgShiftCommand;
import com.hazelcast.command.QuitCommand;
import com.hazelcast.command.SearchCommand;
import com.hazelcast.command.SearchPatternCommand;
import com.hazelcast.command.ShowCommand;
import com.hazelcast.command.TimeShiftCommand;
import com.hazelcast.filter.FileFilter;
import com.hazelcast.filter.LineFilter;
import com.hazelcast.filter.NotFilter;
import com.hazelcast.filter.TokenFilter;
import com.hazelcast.lineTransformer.JitterLogLineTransformer;
import com.hazelcast.lineTransformer.MarkLogLineTransformer;
import com.hazelcast.lineTransformer.ShowFileLogLineTransformer;
import com.hazelcast.logreader.DirectionType;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.fusesource.jansi.Ansi.ansi;

public class CommandReader {
    public static final Command
            PG_UP_CMD = new PgShiftCommand(DirectionType.UP),
            PG_DOWN_CMD = new PgShiftCommand(DirectionType.DOWN),
            HOME_CMD = new JumpCommand(DirectionType.UP),
            END_CMD = new JumpCommand(DirectionType.DOWN),
            PREVIOUS_MATCH_CMD = new SearchCommand(DirectionType.UP),
            NEXT_MATCH_CMD = new SearchCommand(DirectionType.DOWN),
            NO_OP_CMD = new NoopCommand();
    private final BufferedReader reader;
    private boolean commandMode = false;
    private String ttyConfig;

    public CommandReader() {
        printHelp();
        reader = new BufferedReader(new InputStreamReader(System.in));
        ttyConfig = stty("-g");
//        changeTerminalMode(TTYMode.CHAR);
    }

//    public Command next() {
//        while (true) {
//            if (commandMode) {
//                final String line = readLine();
//                changeTerminalMode(TTYMode.CHAR);
//                return parseCommand(line);
//            }
//            final char c = readChar();
//            switch (c) {
//                case 'w':
//                    return PG_UP_CMD;
//                case 's':
//                    return PG_DOWN_CMD;
//                case 'q':
//                    return HOME_CMD;
//                case 'a':
//                    return END_CMD;
//                case 'e':
//                    return PREVIOUS_MATCH_CMD;
//                case 'd':
//                    return NEXT_MATCH_CMD;
//                case '?':
//                    printHelp();
//                    break;
//                case '!':
//                    changeTerminalMode(TTYMode.COMMAND);
//                    break;
//            }
//        }
//    }

    public Command next() {
        while (true) {
            final String line;
            try {
                line = readLine();
            } catch (Exception e) {
                continue;
            }
            if (line == null) {
                continue;
            }
            switch (line) {
                case "w":
                    return PG_UP_CMD;
                case "s":
                    return PG_DOWN_CMD;
                case "q":
                    return HOME_CMD;
                case "a":
                    return END_CMD;
                case "e":
                    return PREVIOUS_MATCH_CMD;
                case "d":
                    return NEXT_MATCH_CMD;
                case "?":
                    printHelp();
                    break;
                default:
                    return parseCommand(line);
            }
        }
    }

    private void printHelp() {
        System.out.println(ansi().eraseScreen());
        PgShiftCommand.printHelp();
        JumpCommand.printHelp();
        System.out.println("! - enter command mode");

        QuitCommand.printHelp();

        NotFilter.printHelp();
        FileFilter.printHelp();
        LineFilter.printHelp();
        TokenFilter.printHelp();

        ClearCommand.printHelp();

        MarkLogLineTransformer.printHelp();
        ShowFileLogLineTransformer.printHelp();
        JitterLogLineTransformer.printHelp();

        LogLineFormatsCommand.printHelp();
        PageSizeCommand.printHelp();

        SearchPatternCommand.printHelp();
        SearchCommand.printHelp();

        TimeShiftCommand.printHelp();
        DumpCommand.printHelp();
        System.out.println("? - this help");
    }

    private static Command parseCommand(String command) {
        final int spaceIdx = command.indexOf(" ");
        final String cmd = spaceIdx > 0 ? command.substring(0, spaceIdx) : command;
        final String args = spaceIdx > 0 ? command.substring(spaceIdx + 1) : "";
        try {
            switch (cmd) {
                case "filter":
                    return new FilterCommand(args);
                case "clear":
                    return new ClearCommand(args);
                case "show":
                    return new ShowCommand(args);
                case "line-format":
                    return new LogLineFormatsCommand();
                case "page-size":
                    return new PageSizeCommand(args);
                case "time-shift":
                    return new TimeShiftCommand(args);
                case "quit":
                    return new QuitCommand();
                case "search":
                    return new SearchPatternCommand(args);
                case "dump":
                    return new DumpCommand(args);
                default:
                    System.out.println("Unknown command " + cmd);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(ansi().fgRed().a(e.getMessage()).reset());
        } catch (Exception e) {
            System.out.println(ansi().fgRed().a("Exception while parsing command").reset());
        }
        return NO_OP_CMD;
    }

    private char readChar() {
        try {
            return (char) reader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
//        changeTerminalMode(TTYMode.COMMAND);
    }

    private String stty(final String args) {
        final String cmd = "stty " + args + " < /dev/tty";
        return exec(new String[]{"sh", "-c", cmd});
    }

    private String exec(final String[] cmd) {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            Process p = Runtime.getRuntime().exec(cmd);
            int c;
            InputStream in = p.getInputStream();

            while ((c = in.read()) != -1) {
                bout.write(c);
            }

            in = p.getErrorStream();

            while ((c = in.read()) != -1) {
                bout.write(c);
            }

            p.waitFor();

            return new String(bout.toByteArray());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void changeTerminalMode(TTYMode mode) {
        switch (mode) {
            case CHAR:
                commandMode = false;
                stty("-icanon min 1");
                stty("-echo");
                break;
            case COMMAND:
                commandMode = true;
                stty(ttyConfig.trim());
                break;
        }
    }

    private enum TTYMode {
        CHAR, COMMAND;
    }
}