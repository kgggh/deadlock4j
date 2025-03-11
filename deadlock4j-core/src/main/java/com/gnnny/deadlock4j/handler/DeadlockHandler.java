package com.gnnny.deadlock4j.handler;

import com.gnnny.deadlock4j.event.DeadlockEvent;

import java.util.List;

public interface DeadlockHandler<T extends DeadlockEvent> {
    void handle(List<T> events);
}
