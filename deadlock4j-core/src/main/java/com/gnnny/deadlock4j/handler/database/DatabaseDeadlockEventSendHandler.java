package com.gnnny.deadlock4j.handler.database;

import com.gnnny.deadlock4j.config.Deadlock4jConfig;
import com.gnnny.deadlock4j.event.DatabaseDeadlockEvent;
import com.gnnny.deadlock4j.event.DeadlockEvent;
import com.gnnny.deadlock4j.transport.DeadlockEventPayload;
import com.gnnny.deadlock4j.transport.EventSender;

import java.util.List;

public class DatabaseDeadlockEventSendHandler implements DatabaseDeadlockHandler {
    private final EventSender sender;
    private final Deadlock4jConfig config;

    public DatabaseDeadlockEventSendHandler(EventSender sender, Deadlock4jConfig config) {
        this.sender = sender;
        this.config = config;
    }

    @Override
    public void handle(List<DatabaseDeadlockEvent> events) {
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
