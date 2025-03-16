package com.github.kgggh.deadlock4j.transport;

public class NoOpConnectionManager implements ConnectionManager<Void> {
    @Override
    public boolean connect() {
        return true;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void close() {
    }

    @Override
    public Void getConnection() {
        return null;
    }
}
