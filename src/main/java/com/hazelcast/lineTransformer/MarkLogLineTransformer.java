package com.hazelcast.lineTransformer;

import com.hazelcast.LogLine;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hazelcast.lineTransformer.MarkLogLineTransformer.Type.FG;
import static org.fusesource.jansi.Ansi.ansi;

public class MarkLogLineTransformer implements LogLineTransformer {
    private final Type type;
    private final Pattern pattern;
    private final Color color;

    public MarkLogLineTransformer(Type type, Pattern pattern, Color color) {
        this.type = type;
        this.pattern = pattern;
        this.color = color;
    }

    public MarkLogLineTransformer(String args) {

        int spaceIdx = args.indexOf(" ");
        this.type = Type.valueOf(args.substring(0, spaceIdx).toUpperCase());

        args = args.substring(spaceIdx + 1);
        spaceIdx = args.indexOf(" ");
        this.color = Color.valueOf(args.substring(0, spaceIdx).toUpperCase());
        try {
            this.pattern = Pattern.compile(args.substring(spaceIdx + 1), Pattern.DOTALL);
        } catch (Exception e) {
            throw new RuntimeException("Unknown pattern " + args);
        }

    }

    @Override
    public LogLine transform(LogLine line) {
        final Matcher m = pattern.matcher(line.line);
        if (m.matches()) {
            for (int i = 0; i < m.groupCount(); i++) {
                final String txt = m.group(i + 1);
                line.line = line.line.replace(txt, an(type, color, txt));
            }
        }
        return line;
    }

    private String an(Type t, Color c, String msg) {
        Ansi a = ansi();
        if (t == FG) {
            a.fg(c);
        } else {
            a.bg(c);
        }
        a.a(msg);
        if (t == FG) {
            a.fgDefault();
        } else {
            a.bgDefault();
        }
        return a.toString();
    }

    public static void printHelp() {
        System.out.println("show mark fg {color} {regex} - mark {regex} with foreground {color}");
        System.out.println("show mark bg {color} {regex} - mark {regex} with background {color}");
    }

    public enum Type {
        FG, BG;
    }
}
