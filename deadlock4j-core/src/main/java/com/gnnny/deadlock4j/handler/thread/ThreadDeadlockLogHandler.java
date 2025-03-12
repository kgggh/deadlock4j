package com.gnnny.deadlock4j.handler.thread;

import com.gnnny.deadlock4j.event.ThreadDeadlockEvent;
import com.gnnny.deadlock4j.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ThreadDeadlockLogHandler implements ThreadDeadlockHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadDeadlockLogHandler.class.getName());

    public void handle(List<ThreadDeadlockEvent> events) {
        if(events == null || events.isEmpty()) {
            return;
        }

        for (ThreadDeadlockEvent event : events) {
            String message = formattedLog(event);
            LOG.warn(message);
        }
    }

    private String formattedLog(ThreadDeadlockEvent event) {
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
