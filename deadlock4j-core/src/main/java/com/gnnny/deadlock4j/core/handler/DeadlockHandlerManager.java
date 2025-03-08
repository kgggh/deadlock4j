package com.gnnny.deadlock4j.core.handler;

public interface DeadlockHandlerManager<T extends DeadlockHandler> {
    void registerHandler(T handler);
    void executeHandlers();
}
