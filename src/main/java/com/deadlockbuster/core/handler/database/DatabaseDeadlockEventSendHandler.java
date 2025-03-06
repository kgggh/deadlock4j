package com.deadlockbuster.core.handler.database;

import com.deadlockbuster.core.event.DatabaseDeadlockEvent;
import com.deadlockbuster.core.event.DeadlockEvent;
import com.deadlockbuster.core.transport.EventSendStrategy;

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
