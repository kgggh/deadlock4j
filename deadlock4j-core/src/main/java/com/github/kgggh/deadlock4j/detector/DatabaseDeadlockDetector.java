package com.github.kgggh.deadlock4j.detector;

import com.github.kgggh.deadlock4j.exception.DatabaseDeadlockExceptionChecker;
import com.github.kgggh.deadlock4j.exception.DatabaseDeadlockExceptionStore;
import com.github.kgggh.deadlock4j.event.DatabaseDeadlockEvent;

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

            if (deadlockSnapshot != null) {
                detectedEvents.add(exceptionChecker.createDeadlockEvent(deadlockSnapshot.getTimestamp(), deadlockSnapshot.getException()));
            }
        }

        return detectedEvents;
    }
}
