package com.gnnny.deadlock4j.transport;

import com.gnnny.deadlock4j.event.DeadlockEvent;

public interface EventSender {
    void send(DeadlockEvent event);
}
