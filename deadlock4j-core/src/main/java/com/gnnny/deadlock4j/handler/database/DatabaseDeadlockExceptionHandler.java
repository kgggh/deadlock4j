package com.gnnny.deadlock4j.handler.database;

import com.gnnny.deadlock4j.exception.DatabaseDeadlockExceptionChecker;
import com.gnnny.deadlock4j.exception.DatabaseDeadlockExceptionStore;

public class DatabaseDeadlockExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final DatabaseDeadlockExceptionChecker deadlockExceptionChecker;

    public DatabaseDeadlockExceptionHandler(DatabaseDeadlockExceptionChecker deadlockExceptionChecker) {
        this.deadlockExceptionChecker = deadlockExceptionChecker;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (deadlockExceptionChecker.isDeadlockException(e)) {
            DatabaseDeadlockExceptionStore.add(e);
        }

        throw new RuntimeException(e);
    }
}
