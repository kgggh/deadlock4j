package com.github.kgggh.deadlock4j.transport;

public interface EventSender {
    void send(DeadlockEventPayload eventPayload);
}
