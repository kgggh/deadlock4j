package com.deadlockbuster.core.handler;

import com.deadlockbuster.core.event.DeadlockEvent;

import java.util.List;

public interface DeadlockHandler<T extends DeadlockEvent> {
    void handle(List<T> events);
}
