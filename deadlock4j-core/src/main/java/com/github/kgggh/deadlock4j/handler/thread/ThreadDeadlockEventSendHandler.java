package com.github.kgggh.deadlock4j.handler.thread;

import com.github.kgggh.deadlock4j.config.Deadlock4jConfig;
import com.github.kgggh.deadlock4j.transport.EventSender;
import com.github.kgggh.deadlock4j.event.DeadlockEvent;
import com.github.kgggh.deadlock4j.event.ThreadDeadlockEvent;
import com.github.kgggh.deadlock4j.transport.DeadlockEventPayload;

import java.util.List;

public class ThreadDeadlockEventSendHandler implements ThreadDeadlockHandler {
    private final EventSender sender;
    private final Deadlock4jConfig deadlock4jConfig;

    public ThreadDeadlockEventSendHandler(EventSender sender, Deadlock4jConfig deadlock4jConfig) {
        this.sender = sender;
        this.deadlock4jConfig = deadlock4jConfig;
    }

    @Override
    public void handle(List<ThreadDeadlockEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        for (DeadlockEvent event : events) {
            sender.send(convertToPayload(event));
        }
    }

    private DeadlockEventPayload convertToPayload(DeadlockEvent event) {
        return new DeadlockEventPayload(deadlock4jConfig.getInstanceId(), event);
    }
}
