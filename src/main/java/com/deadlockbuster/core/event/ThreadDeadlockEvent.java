package com.deadlockbuster.core.event;

/**
 * 스레드 데드락 발생 시 생성되는 이벤트 객체
 */
public class ThreadDeadlockEvent extends DeadlockEvent {
    private final String threadName;
    private final long threadId;
    private final String threadState;

    public ThreadDeadlockEvent(String threadName, long threadId, String threadState) {
        super(DeadlockType.THREAD);
        this.threadName = threadName;
        this.threadId = threadId;
        this.threadState = threadState;
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

    @Override
    public String toString() {
        return "ThreadDeadlockEvent{" +
            "threadName='" + threadName + '\'' +
            ", threadId=" + threadId +
            ", threadState='" + threadState + '\'' +
            '}';
    }
}

