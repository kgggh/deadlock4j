package com.gnnny.deadlock4j.core.detector;

import com.gnnny.deadlock4j.core.event.DatabaseDeadlockEvent;
import com.gnnny.deadlock4j.core.exception.DatabaseDeadlockExceptionChecker;
import com.gnnny.deadlock4j.core.exception.DatabaseDeadlockExceptionStore;

import java.util.ArrayList;
import java.util.List;

public class DatabaseDeadlockDetector implements DeadlockDetector<DatabaseDeadlockEvent> {
    private final DatabaseDeadlockExceptionChecker exceptionChecker;

    public DatabaseDeadlockDetector(DatabaseDeadlockExceptionChecker exceptionChecker) {
        this.exceptionChecker = exceptionChecker;
    }

    @Override
    public List<DatabaseDeadlockEvent> detect() {
        List<DatabaseDeadlockEvent> detectedEvents = new ArrayList<>();

        for (Throwable recentException : DatabaseDeadlockExceptionStore.peekAll()) {
            if (exceptionChecker.isDeadlockException(recentException)) {
                detectedEvents.add(exceptionChecker.createDeadlockEvent(recentException));
            }
        }

        return detectedEvents;
    }
}
