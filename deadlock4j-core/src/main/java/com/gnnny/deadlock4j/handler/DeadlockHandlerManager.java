package com.gnnny.deadlock4j.handler;

public interface DeadlockHandlerManager<T extends DeadlockHandler> {
    void registerHandler(T handler);
    void executeHandlers();
}
