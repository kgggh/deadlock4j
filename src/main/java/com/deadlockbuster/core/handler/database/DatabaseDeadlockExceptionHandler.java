package com.deadlockbuster.core.handler.database;

import com.deadlockbuster.core.exception.DatabaseDeadlockExceptionChecker;
import com.deadlockbuster.core.exception.DatabaseDeadlockExceptionStore;

public class DatabaseDeadlockExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final DatabaseDeadlockExceptionChecker deadlockExceptionChecker;

    public DatabaseDeadlockExceptionHandler(DatabaseDeadlockExceptionChecker deadlockExceptionChecker) {
        this.deadlockExceptionChecker = deadlockExceptionChecker;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!deadlockExceptionChecker.isDeadlockException(e)) {
            return;
        }

        DatabaseDeadlockExceptionStore.add(e);
    }
}
