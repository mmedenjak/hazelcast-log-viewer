package com.hazelcast.command;

import com.hazelcast.Coordinator;

public class PageSizeCommand implements Command {
    private final int newPageSize;

    public PageSizeCommand(String args) {
        this.newPageSize = (args != null && !args.trim().isEmpty()) ? Integer.valueOf(args) : -1;
    }

    @Override
    public void execute(Coordinator coordinator) {
        if (newPageSize != -1) {
            coordinator.setPageSize(newPageSize);
        } else {
            System.out.println("Current page size " + coordinator.getPageSize());
        }
    }

    public static void printHelp() {
        System.out.println("page-size - show current page size");
        System.out.println("page size {size} - change page size to {size}");
    }
}
