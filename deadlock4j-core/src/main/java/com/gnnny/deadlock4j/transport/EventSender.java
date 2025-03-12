package com.gnnny.deadlock4j.transport;

public interface EventSender {
    void send(DeadlockEventPayload eventPayload);
}
