package com.gnnny.deadlock4j.handler.thread;

import com.gnnny.deadlock4j.config.Deadlock4jConfig;
import com.gnnny.deadlock4j.event.DeadlockEvent;
import com.gnnny.deadlock4j.event.ThreadDeadlockEvent;
import com.gnnny.deadlock4j.transport.DeadlockEventPayload;
import com.gnnny.deadlock4j.transport.EventSender;

import java.util.List;

public class ThreadDeadlockEventSendHandler implements ThreadDeadlockHandler {
    private final EventSender sender;
    private final Deadlock4jConfig config;

    public ThreadDeadlockEventSendHandler(EventSender sender, Deadlock4jConfig config) {
        this.sender = sender;
        this.config = config;
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
        return new DeadlockEventPayload(config.getInstanceId(), event);
    }
}
