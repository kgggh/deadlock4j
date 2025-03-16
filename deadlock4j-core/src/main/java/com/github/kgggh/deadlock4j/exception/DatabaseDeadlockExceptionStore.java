package com.github.kgggh.deadlock4j.exception;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DatabaseDeadlockExceptionStore {
    private static final ConcurrentLinkedQueue<DatabaseDeadlockSnapshot> recentDatabaseExceptions = new ConcurrentLinkedQueue<>();

    public static void add(Throwable e) {
        DatabaseDeadlockSnapshot snapshot = new DatabaseDeadlockSnapshot(e);
        recentDatabaseExceptions.offer(snapshot);
    }

    public static List<DatabaseDeadlockSnapshot> getAll() {
        return List.copyOf(recentDatabaseExceptions);
    }

    public static void clear() {
        recentDatabaseExceptions.clear();
    }

    public static DatabaseDeadlockSnapshot next() {
        return recentDatabaseExceptions.poll();
    }

    public static boolean isEmpty() {
        return recentDatabaseExceptions.isEmpty();
    }

    public static class DatabaseDeadlockSnapshot {
        private final Throwable exception;
        private final long timestamp;

        public DatabaseDeadlockSnapshot(Throwable exception) {
            this.exception = exception;
            this.timestamp = System.currentTimeMillis();
        }

        public Throwable getException() {
            return exception;
        }

        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return "DatabaseDeadlockSnapshot{" +
                "exception=" + exception +
                ", timestamp=" + timestamp +
                '}';
        }
    }
}
