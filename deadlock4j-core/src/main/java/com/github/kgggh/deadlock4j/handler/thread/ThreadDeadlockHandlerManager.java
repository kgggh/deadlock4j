package com.github.kgggh.deadlock4j.handler.thread;

import com.github.kgggh.deadlock4j.detector.DeadlockDetector;
import com.github.kgggh.deadlock4j.event.ThreadDeadlockEvent;
import com.github.kgggh.deadlock4j.handler.DeadlockHandlerManager;

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
    public void processHandlers() {
        List<ThreadDeadlockEvent> events = detector.detect();
        if (events == null || events.isEmpty()) {
            return;
        }

        for (ThreadDeadlockHandler handler : handlers) {
            handler.handle(events);
        }
    }

    @Override
    public void stop() {
        handlers.clear();
    }
}
