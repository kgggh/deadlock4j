package com.gnnny.deadlock4j.core.transport;

import com.gnnny.deadlock4j.core.event.DeadlockEvent;

public interface EventSendStrategy {
    void send(DeadlockEvent event);
}
