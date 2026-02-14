package com.github.kgggh.deadlock4j.handler.database;

import com.github.kgggh.deadlock4j.detector.DeadlockDetector;
import com.github.kgggh.deadlock4j.event.DatabaseDeadlockEvent;
import com.github.kgggh.deadlock4j.exception.DatabaseDeadlockExceptionChecker;
import com.github.kgggh.deadlock4j.handler.AbstractDeadlockHandlerManager;

public class DatabaseDeadlockHandlerManager extends AbstractDeadlockHandlerManager<DatabaseDeadlockHandler, DatabaseDeadlockEvent> {

    public DatabaseDeadlockHandlerManager(DeadlockDetector<DatabaseDeadlockEvent> detector) {
        super(detector);
        DatabaseDeadlockExceptionHandler exceptionHandler = new DatabaseDeadlockExceptionHandler(new DatabaseDeadlockExceptionChecker());
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
    }
}
