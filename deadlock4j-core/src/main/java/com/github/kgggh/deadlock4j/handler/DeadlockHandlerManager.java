package com.github.kgggh.deadlock4j.handler;

public interface DeadlockHandlerManager<T extends DeadlockHandler> {
    void registerHandler(T handler);
    void processHandlers();
    void stop();
}
