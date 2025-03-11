package com.gnnny.deadlock4j.exception;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DatabaseDeadlockExceptionStore {
    private static final int MAX_QUEUE_SIZE = 30;
    private static final BlockingQueue<Throwable> recentDatabaseExceptions = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

    public static void add(Throwable e) {
        if (!recentDatabaseExceptions.offer(e)) {
            recentDatabaseExceptions.poll();
            recentDatabaseExceptions.offer(e);
        }
    }

    public static List<Throwable> getAll() {
        return List.copyOf(recentDatabaseExceptions);
    }

    public static void clear() {
        recentDatabaseExceptions.clear();
    }

    public static Throwable next() {
        return recentDatabaseExceptions.poll();
    }

    public static boolean isEmpty() {
        return recentDatabaseExceptions.isEmpty();
    }
}
