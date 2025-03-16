package com.github.kgggh.deadlock4j.event;

public abstract class DeadlockEvent {

    public enum DeadlockType {
        THREAD, DATABASE
    }

    private final DeadlockType type;
    private final long timestamp;

    protected DeadlockEvent(DeadlockType type, long timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }

    public DeadlockType getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
