package com.deadlockbuster.core.exception;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DatabaseDeadlockExceptionStore {
    private static final Queue<Throwable> recentDatabaseExceptions = new ConcurrentLinkedQueue<>();
    private static final int MAX_QUEUE_SIZE = 30;

    public static void add(Throwable e) {
        recentDatabaseExceptions.add(e);

        while (recentDatabaseExceptions.size() > MAX_QUEUE_SIZE) {
            recentDatabaseExceptions.poll();
        }
    }

    public static List<Throwable> peekAll() {
        return List.copyOf(recentDatabaseExceptions);
    }

    public static void clear() {
        recentDatabaseExceptions.clear();
    }
}
