package com.deadlockbuster.core.handler;

public interface DeadlockHandlerManager<T extends DeadlockHandler> {
    void registerHandler(T handler);
    void executeHandlers();
}
