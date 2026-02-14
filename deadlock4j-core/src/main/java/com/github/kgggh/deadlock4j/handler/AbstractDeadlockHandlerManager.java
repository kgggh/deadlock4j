package com.github.kgggh.deadlock4j.handler;

import com.github.kgggh.deadlock4j.detector.DeadlockDetector;
import com.github.kgggh.deadlock4j.event.DeadlockEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDeadlockHandlerManager<H extends DeadlockHandler<E>, E extends DeadlockEvent>
    implements DeadlockHandlerManager<H> {

    private final List<H> handlers = new ArrayList<>();
    private final DeadlockDetector<E> detector;

    protected AbstractDeadlockHandlerManager(DeadlockDetector<E> detector) {
        this.detector = detector;
    }

    @Override
    public void registerHandler(H handler) {
        handlers.add(handler);
    }

    @Override
    public void processHandlers() {
        List<E> events = detector.detect();
        if (events == null || events.isEmpty()) {
            return;
        }

        for (H handler : handlers) {
            handler.handle(events);
        }
    }

    @Override
    public void stop() {
        handlers.clear();
    }
}
