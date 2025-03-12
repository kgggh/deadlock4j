package com.gnnny.deadlock4j.event;

public class ThreadDeadlockEvent extends DeadlockEvent {
    private final String threadName;
    private final long threadId;
    private final String threadState;
    private final long blockedCount;
    private final long waitedCount;
    private final String lockName;
    private final long lockOwnerId;
    private final String lockOwnerName;
    private final String stackTrace;

    public ThreadDeadlockEvent(long timestamp, String threadName, long threadId, String threadState, long blockedCount, long waitedCount, String lockName, long lockOwnerId, String lockOwnerName, String stackTrace) {
        super(DeadlockType.THREAD, timestamp);
        this.threadName = threadName;
        this.threadId = threadId;
        this.threadState = threadState;
        this.blockedCount = blockedCount;
        this.waitedCount = waitedCount;
        this.lockName = lockName;
        this.lockOwnerId = lockOwnerId;
        this.lockOwnerName = lockOwnerName;
        this.stackTrace = stackTrace;
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

    public long getBlockedCount() {
        return blockedCount;
    }

    public long getWaitedCount() {
        return waitedCount;
    }

    public String getLockName() {
        return lockName;
    }

    public long getLockOwnerId() {
        return lockOwnerId;
    }

    public String getLockOwnerName() {
        return lockOwnerName;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    @Override
    public String toString() {
        return "ThreadDeadlockEvent{" +
            "threadName='" + threadName + '\'' +
            ", threadId=" + threadId +
            ", threadState='" + threadState + '\'' +
            ", blockedCount=" + blockedCount +
            ", waitedCount=" + waitedCount +
            ", lockName='" + lockName + '\'' +
            ", lockOwnerId=" + lockOwnerId +
            ", lockOwnerName='" + lockOwnerName + '\'' +
            ", stackTrace=" + stackTrace +
            '}';
    }
}

