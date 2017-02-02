package com.hazelcast;

import java.io.IOException;

public class LogReaderApplication {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Did not specify and files");
            System.exit(1);
        }
        new Coordinator(args).start();
    }
}
