package com.hazelcast.filter;

import com.hazelcast.LogLine;

import java.util.regex.Pattern;

public class TokenFilter implements Filter {
    private final Pattern pattern;
    private final int tokenIdx;

    public TokenFilter(String args) {
        final int spaceIdx = args.indexOf(" ");
        try {
            this.tokenIdx = Integer.valueOf(args.substring(0, spaceIdx));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Illegal index");
        }
        try {
            this.pattern = Pattern.compile(args.substring(spaceIdx + 1));
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal pattern");
        }
    }

    @Override
    public boolean filter(LogLine line) {
        try {
            return line.tokens.length > tokenIdx && pattern.matcher(line.tokens[tokenIdx]).matches();
        } catch (Exception e) {
            return false;
        }
    }

    public static void printHelp() {
        System.out.println("filter token {id} {regex} - show lines which where token {id} matches {regex}");
    }
}
