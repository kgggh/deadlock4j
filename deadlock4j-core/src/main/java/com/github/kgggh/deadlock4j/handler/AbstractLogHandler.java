package com.github.kgggh.deadlock4j.handler;

import com.github.kgggh.deadlock4j.event.DeadlockEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractLogHandler<T extends DeadlockEvent> implements DeadlockHandler<T> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    protected abstract String formattedLog(T event);

    @Override
    public void handle(List<T> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        for (T event : events) {
            String message = formattedLog(event);
            log.warn(message);
        }
    }
}
