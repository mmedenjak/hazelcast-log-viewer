package com.hazelcast;

import java.util.concurrent.TimeUnit;

public class ProgressIndicator {
    private volatile boolean running = true;
    private Thread thread;

    public void start() {
        this.thread = new Thread(() -> {
            System.out.print("Working");
            while (running) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                }
                System.out.print(".");
            }
            System.out.print("\n");
        });
        thread.start();
    }

    public void stop() {
        try {
            running = false;
            thread.join();
        } catch (InterruptedException e) {
        }
    }
}
