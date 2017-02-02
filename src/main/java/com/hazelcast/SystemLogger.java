package com.hazelcast;

public class SystemLogger implements Logger {
    @Override
    public void error(String msg, Throwable t) {
        System.err.println(msg);
    }
}
