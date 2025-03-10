package com.gnnny.deadlock4j.core.handler.database;

import com.gnnny.deadlock4j.core.detector.DeadlockDetector;
import com.gnnny.deadlock4j.core.event.DatabaseDeadlockEvent;
import com.gnnny.deadlock4j.core.exception.DatabaseDeadlockExceptionChecker;
import com.gnnny.deadlock4j.core.handler.DeadlockHandlerManager;

import java.util.ArrayList;
import java.util.List;

public class DatabaseDeadlockHandlerManager implements DeadlockHandlerManager<DatabaseDeadlockHandler> {
    private final List<DatabaseDeadlockHandler> handlers = new ArrayList<>();
    private final DeadlockDetector<DatabaseDeadlockEvent> detector;

    public DatabaseDeadlockHandlerManager(DeadlockDetector<DatabaseDeadlockEvent> detector) {
        this.detector = detector;
        DatabaseDeadlockExceptionHandler exceptionHandler = new DatabaseDeadlockExceptionHandler(new DatabaseDeadlockExceptionChecker());
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
    }

    @Override
    public void registerHandler(DatabaseDeadlockHandler handler) {
        this.handlers.add(handler);
    }

    @Override
    public void executeHandlers() {
        List<DatabaseDeadlockEvent> events = detector.detect();
        if (events == null || events.isEmpty()) {
            return;
        }

        for (DatabaseDeadlockHandler handler : handlers) {
            handler.handle(events);
        }
    }
}
