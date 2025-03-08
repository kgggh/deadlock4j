package com.gnnny.deadlock4j.core.handler.database;

import com.gnnny.deadlock4j.core.exception.DatabaseDeadlockExceptionChecker;
import com.gnnny.deadlock4j.core.exception.DatabaseDeadlockExceptionStore;

public class DatabaseDeadlockExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final DatabaseDeadlockExceptionChecker deadlockExceptionChecker;

    public DatabaseDeadlockExceptionHandler(DatabaseDeadlockExceptionChecker deadlockExceptionChecker) {
        this.deadlockExceptionChecker = deadlockExceptionChecker;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!deadlockExceptionChecker.isDeadlockException(e)) {
            throw new RuntimeException(e);
        }

        DatabaseDeadlockExceptionStore.add(e);
    }
}
