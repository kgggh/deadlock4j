package com.gnnny.deadlock4j.core.event;

public abstract class DeadlockEvent {

    public enum DeadlockType {
        THREAD, DATABASE
    }

    private final DeadlockType type;
    private final long timestamp;

    protected DeadlockEvent(DeadlockType type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public DeadlockType getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
