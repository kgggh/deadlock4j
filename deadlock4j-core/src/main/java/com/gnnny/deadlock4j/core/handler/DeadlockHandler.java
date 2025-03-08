package com.gnnny.deadlock4j.core.handler;

import com.gnnny.deadlock4j.core.event.DeadlockEvent;

import java.util.List;

public interface DeadlockHandler<T extends DeadlockEvent> {
    void handle(List<T> events);
}
