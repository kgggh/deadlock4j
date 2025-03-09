package com.gnnny.deadlock4j.core.event;

import java.time.Instant;

public abstract class DeadlockEvent {

    public enum DeadlockType {
        THREAD, DATABASE
    }

    private final DeadlockType type;
    private final Instant timestamp;


    protected DeadlockEvent(DeadlockType type) {
        this.type = type;
        this.timestamp = Instant.now();
    }

    public DeadlockType getType() {
        return type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
