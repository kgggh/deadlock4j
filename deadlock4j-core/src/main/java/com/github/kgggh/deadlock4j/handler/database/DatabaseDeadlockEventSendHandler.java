package com.github.kgggh.deadlock4j.handler.database;

import com.github.kgggh.deadlock4j.config.Deadlock4jConfig;
import com.github.kgggh.deadlock4j.handler.AbstractEventSendHandler;
import com.github.kgggh.deadlock4j.event.DatabaseDeadlockEvent;
import com.github.kgggh.deadlock4j.transport.EventSender;

public class DatabaseDeadlockEventSendHandler extends AbstractEventSendHandler<DatabaseDeadlockEvent>
    implements DatabaseDeadlockHandler {

    public DatabaseDeadlockEventSendHandler(EventSender eventSender, Deadlock4jConfig deadlock4jConfig) {
        super(eventSender, deadlock4jConfig);
    }
}
