package com.gnnny.deadlock4j.handler.database;

import com.gnnny.deadlock4j.event.DatabaseDeadlockEvent;
import com.gnnny.deadlock4j.event.DeadlockEvent;
import com.gnnny.deadlock4j.transport.EventSendStrategy;

import java.util.List;

public class DatabaseDeadlockEventSendHandler implements DatabaseDeadlockHandler {
    private final EventSendStrategy sendStrategy;

    public DatabaseDeadlockEventSendHandler(EventSendStrategy sendStrategy) {
        this.sendStrategy = sendStrategy;
    }


    @Override
    public void handle(List<DatabaseDeadlockEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        for (DeadlockEvent event : events) {
            sendStrategy.send(event);
        }
    }
}
