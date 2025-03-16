package com.github.kgggh.deadlock4j.handler.database;

import com.github.kgggh.deadlock4j.detector.DeadlockDetector;
import com.github.kgggh.deadlock4j.exception.DatabaseDeadlockExceptionChecker;
import com.github.kgggh.deadlock4j.event.DatabaseDeadlockEvent;
import com.github.kgggh.deadlock4j.handler.DeadlockHandlerManager;

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
    public void processHandlers() {
        List<DatabaseDeadlockEvent> events = detector.detect();
        if (events == null || events.isEmpty()) {
            return;
        }

        for (DatabaseDeadlockHandler handler : handlers) {
            handler.handle(events);
        }
    }

    @Override
    public void stop() {
        handlers.clear();
    }
}
