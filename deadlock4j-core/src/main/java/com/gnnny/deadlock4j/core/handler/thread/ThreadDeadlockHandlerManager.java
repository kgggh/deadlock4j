package com.gnnny.deadlock4j.core.handler.thread;

import com.gnnny.deadlock4j.core.detector.DeadlockDetector;
import com.gnnny.deadlock4j.core.event.ThreadDeadlockEvent;
import com.gnnny.deadlock4j.core.handler.DeadlockHandlerManager;

import java.util.ArrayList;
import java.util.List;

public class ThreadDeadlockHandlerManager implements DeadlockHandlerManager<ThreadDeadlockHandler> {
    private final List<ThreadDeadlockHandler> handlers = new ArrayList<>();
    private final DeadlockDetector<ThreadDeadlockEvent> detector;

    public ThreadDeadlockHandlerManager(DeadlockDetector<ThreadDeadlockEvent> detector) {
        this.detector = detector;
    }

    @Override
    public void registerHandler(ThreadDeadlockHandler handler) {
        handlers.add(handler);
    }

    @Override
    public void executeHandlers() {
        List<ThreadDeadlockEvent> events = detector.detect();
        if (events == null || events.isEmpty()) {
            return;
        }

        for (ThreadDeadlockHandler handler : handlers) {
            handler.handle(events);
        }
    }
}
