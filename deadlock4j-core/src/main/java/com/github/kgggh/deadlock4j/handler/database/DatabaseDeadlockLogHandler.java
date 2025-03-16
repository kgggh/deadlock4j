package com.github.kgggh.deadlock4j.handler.database;

import com.github.kgggh.deadlock4j.event.DatabaseDeadlockEvent;
import com.github.kgggh.deadlock4j.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DatabaseDeadlockLogHandler implements DatabaseDeadlockHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseDeadlockLogHandler.class.getName());

    @Override
    public void handle(List<DatabaseDeadlockEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        for (DatabaseDeadlockEvent event : events) {
            String message = formattedLog(event);
            LOG.warn(message);
        }
    }

    private String formattedLog(DatabaseDeadlockEvent event) {
        return """
        
        [DEADLOCK DETECTED]
        ──────────────────────────────────────────
        Type           : %s
        Timestamp      : %s
        Exception Name : %s
        Sql State      : %s
        Reason         : %s
        ──────────────────────────────────────────
        """.formatted(
            event.getType(),
            DateTimeUtil.formatIso(event.getTimestamp()),
            event.getExceptionName(),
            event.getSqlState(),
            event.getReason()
        );
    }
}
