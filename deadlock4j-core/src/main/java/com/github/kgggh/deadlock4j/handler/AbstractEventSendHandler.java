package com.github.kgggh.deadlock4j.handler;

import com.github.kgggh.deadlock4j.config.Deadlock4jConfig;
import com.github.kgggh.deadlock4j.event.DeadlockEvent;
import com.github.kgggh.deadlock4j.transport.DeadlockEventPayload;
import com.github.kgggh.deadlock4j.transport.EventSender;

import java.util.List;

public abstract class AbstractEventSendHandler<T extends DeadlockEvent> implements DeadlockHandler<T> {
    private final EventSender eventSender;
    private final Deadlock4jConfig config;

    protected AbstractEventSendHandler(EventSender eventSender, Deadlock4jConfig config) {
        this.eventSender = eventSender;
        this.config = config;
    }

    @Override
    public void handle(List<T> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        for (DeadlockEvent event : events) {
            eventSender.send(convertToPayload(event));
        }
    }

    private DeadlockEventPayload convertToPayload(DeadlockEvent event) {
        return new DeadlockEventPayload(config.getInstanceId(), event);
    }
}
