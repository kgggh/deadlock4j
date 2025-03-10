package com.gnnny.deadlock4j.core.handler.thread;

import com.gnnny.deadlock4j.core.event.ThreadDeadlockEvent;
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
        ──────────────────────────────────────────
        """.formatted(
            event.getType(),
            DateTimeUtil.formatIso(event.getTimestamp()),
            event.getThreadName(),
            event.getThreadId(),
            event.getThreadState()
        );
    }
}
