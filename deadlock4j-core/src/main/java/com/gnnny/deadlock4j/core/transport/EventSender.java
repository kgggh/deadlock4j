package com.gnnny.deadlock4j.core.transport;

import com.gnnny.deadlock4j.core.event.DeadlockEvent;

public interface EventSender {
    void send(DeadlockEvent event);
}
