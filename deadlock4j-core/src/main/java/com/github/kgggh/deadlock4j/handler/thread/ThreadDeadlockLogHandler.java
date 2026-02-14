package com.github.kgggh.deadlock4j.handler.thread;

import com.github.kgggh.deadlock4j.event.ThreadDeadlockEvent;
import com.github.kgggh.deadlock4j.handler.AbstractLogHandler;
import com.github.kgggh.deadlock4j.util.DateTimeUtil;

public class ThreadDeadlockLogHandler extends AbstractLogHandler<ThreadDeadlockEvent>
    implements ThreadDeadlockHandler {

    @Override
    protected String formattedLog(ThreadDeadlockEvent event) {
        return """

        [DEADLOCK DETECTED]
        ──────────────────────────────────────────
        Type           : %s
        Timestamp      : %s
        Thread Name    : %s
        Thread ID      : %d
        Thread State   : %s
        Blocked Count  : %d
        Waited Count   : %d
        Lock Name      : %s
        Lock Owner ID  : %d
        Lock Owner Name: %s
        ──────────────────────────────────────────
        Stack Trace:
        %s
        ──────────────────────────────────────────
        """.formatted(
            event.getType(),
            DateTimeUtil.formatIso(event.getTimestamp()),
            event.getThreadName(),
            event.getThreadId(),
            event.getThreadState(),
            event.getBlockedCount(),
            event.getWaitedCount(),
            event.getLockName(),
            event.getLockOwnerId(),
            event.getLockOwnerName(),
            event.getStackTrace()
        );
    }
}
