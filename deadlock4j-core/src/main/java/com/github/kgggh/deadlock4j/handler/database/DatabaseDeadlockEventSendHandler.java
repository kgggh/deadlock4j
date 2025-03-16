package com.github.kgggh.deadlock4j.handler.database;

import com.github.kgggh.deadlock4j.config.Deadlock4jConfig;
import com.github.kgggh.deadlock4j.transport.EventSender;
import com.github.kgggh.deadlock4j.event.DatabaseDeadlockEvent;
import com.github.kgggh.deadlock4j.event.DeadlockEvent;
import com.github.kgggh.deadlock4j.transport.DeadlockEventPayload;

import java.util.List;

public class DatabaseDeadlockEventSendHandler implements DatabaseDeadlockHandler {
    private final EventSender eventSender;
    private final Deadlock4jConfig deadlock4jConfig;

    public DatabaseDeadlockEventSendHandler(EventSender eventSender, Deadlock4jConfig deadlock4jConfig) {
        this.eventSender = eventSender;
        this.deadlock4jConfig = deadlock4jConfig;
    }

    @Override
    public void handle(List<DatabaseDeadlockEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        for (DeadlockEvent event : events) {
            eventSender.send(convertToPayload(event));
        }
    }

    private DeadlockEventPayload convertToPayload(DeadlockEvent event) {
        return new DeadlockEventPayload(deadlock4jConfig.getInstanceId(), event);
    }
}
