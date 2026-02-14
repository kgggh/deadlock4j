package com.github.kgggh.deadlock4j.handler.database;

import com.github.kgggh.deadlock4j.event.DatabaseDeadlockEvent;
import com.github.kgggh.deadlock4j.handler.AbstractLogHandler;
import com.github.kgggh.deadlock4j.util.DateTimeUtil;

public class DatabaseDeadlockLogHandler extends AbstractLogHandler<DatabaseDeadlockEvent>
    implements DatabaseDeadlockHandler {

    @Override
    protected String formattedLog(DatabaseDeadlockEvent event) {
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
