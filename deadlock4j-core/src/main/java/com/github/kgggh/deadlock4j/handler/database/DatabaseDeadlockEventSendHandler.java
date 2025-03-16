package com.github.kgggh.deadlock4j.handler.database;

import com.github.kgggh.deadlock4j.config.Deadlock4jConfig;
import com.github.kgggh.deadlock4j.transport.EventSender;
import com.github.kgggh.deadlock4j.event.DatabaseDeadlockEvent;
import com.github.kgggh.deadlock4j.event.DeadlockEvent;
import com.github.kgggh.deadlock4j.transport.DeadlockEventPayload;

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
