package com.deadlockbuster.core.handler.thread;

import com.deadlockbuster.core.event.DeadlockEvent;
import com.deadlockbuster.core.event.ThreadDeadlockEvent;
import com.deadlockbuster.core.transport.EventSendStrategy;

import java.util.List;

public class ThreadDeadlockEventSendHandler implements ThreadDeadlockHandler {
    private final EventSendStrategy sendStrategy;

    public ThreadDeadlockEventSendHandler(EventSendStrategy sendStrategy) {
        this.sendStrategy = sendStrategy;
    }

    @Override
    public void handle(List<ThreadDeadlockEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        for (DeadlockEvent event : events) {
            sendStrategy.send(event);
        }
    }
}
