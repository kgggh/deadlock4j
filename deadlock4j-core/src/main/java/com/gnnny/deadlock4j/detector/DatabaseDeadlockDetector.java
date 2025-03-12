package com.gnnny.deadlock4j.detector;

import com.gnnny.deadlock4j.event.DatabaseDeadlockEvent;
import com.gnnny.deadlock4j.exception.DatabaseDeadlockExceptionChecker;
import com.gnnny.deadlock4j.exception.DatabaseDeadlockExceptionStore;

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

        while (!DatabaseDeadlockExceptionStore.isEmpty()) {
            DatabaseDeadlockExceptionStore.DatabaseDeadlockSnapshot deadlockSnapshot = DatabaseDeadlockExceptionStore.next();

            if (deadlockSnapshot != null && exceptionChecker.isDeadlockException(deadlockSnapshot.getException())) {
                detectedEvents.add(exceptionChecker.createDeadlockEvent(deadlockSnapshot.getTimestamp(), deadlockSnapshot.getException()));
            }
        }

        return detectedEvents;
    }
}
