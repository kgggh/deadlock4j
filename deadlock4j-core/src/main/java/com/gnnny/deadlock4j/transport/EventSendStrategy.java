package com.gnnny.deadlock4j.transport;

import com.gnnny.deadlock4j.event.DeadlockEvent;

public interface EventSendStrategy {
    void send(DeadlockEvent event);
}
