package com.github.kgggh.deadlock4j.handler.database;

import com.github.kgggh.deadlock4j.exception.DatabaseDeadlockExceptionChecker;
import com.github.kgggh.deadlock4j.exception.DatabaseDeadlockExceptionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseDeadlockExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseDeadlockExceptionHandler.class);
    private final DatabaseDeadlockExceptionChecker deadlockExceptionChecker;

    public DatabaseDeadlockExceptionHandler(DatabaseDeadlockExceptionChecker deadlockExceptionChecker) {
        this.deadlockExceptionChecker = deadlockExceptionChecker;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if(deadlockExceptionChecker.isDeadlockException(e)) {
            DatabaseDeadlockExceptionStore.add(e);
        }

        LOG.error("Uncaught exception in thread {}", t.getName(), e);
    }
}
