package com.deadlockbuster.core.handler.database;

import com.deadlockbuster.core.detector.DeadlockDetector;
import com.deadlockbuster.core.event.DatabaseDeadlockEvent;
import com.deadlockbuster.core.exception.DatabaseDeadlockExceptionChecker;
import com.deadlockbuster.core.exception.DatabaseDeadlockExceptionStore;
import com.deadlockbuster.core.handler.DeadlockHandlerManager;

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

        DatabaseDeadlockExceptionStore.clear();
    }
}
