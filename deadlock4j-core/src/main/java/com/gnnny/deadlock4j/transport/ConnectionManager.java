package com.gnnny.deadlock4j.transport;

public interface ConnectionManager<T> {
    boolean connect();
    boolean isConnected();
    void close();
    T getConnection();
}
