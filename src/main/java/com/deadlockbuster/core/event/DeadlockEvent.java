package com.deadlockbuster.core.event;

import java.time.Instant;

public class DeadlockEvent {

    public enum DeadlockType {
        THREAD, DATABASE
    }

    private final DeadlockType type;
    private final String threadName;
    private final long threadId;
    private String threadState;
    private final Instant timestamp;

    public DeadlockEvent(DeadlockType type, String threadName, long threadId, String threadState) {
        this.type = type;
        this.threadName = threadName;
        this.threadId = threadId;
        this.threadState = threadState;
        this.timestamp = Instant.now();
    }

    public DeadlockType getType() {
        return type;
    }

    public String getThreadName() {
        return threadName;
    }

    public long getThreadId() {
        return threadId;
    }

    public String getThreadState() {
        return threadState;
    }

    public void setThreadState(String threadState) {
        this.threadState = threadState;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
