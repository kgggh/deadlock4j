package com.github.kgggh.deadlock4j.transport.heartbeat;

import com.github.kgggh.deadlock4j.transport.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartbeatManager {
    private static final Logger LOG = LoggerFactory.getLogger(HeartbeatManager.class);
    private final int heartBeatInterval;
    private final HeartbeatSender heartbeatSender;
    private final ConnectionManager<?> connectionManager;
    private final ScheduledExecutorService scheduler;

    public HeartbeatManager(int heartBeatInterval, HeartbeatSender heartbeatSender, ConnectionManager<?> connectionManager, ScheduledExecutorService scheduler) {
        if (heartBeatInterval < 500) {
            throw new IllegalArgumentException("Heartbeat interval must be greater than 500. Given: " + heartBeatInterval);
        }

        this.heartBeatInterval = heartBeatInterval;
        this.heartbeatSender = heartbeatSender;
        this.connectionManager = connectionManager;
        this.scheduler = scheduler;
    }

    public ConnectionManager<?> getConnectionManager() {
        return connectionManager;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::heartbeatTask, 0, heartBeatInterval, TimeUnit.MILLISECONDS);
    }

    private void heartbeatTask() {
        try {
            boolean connected = connectionManager.isConnected() || connectionManager.connect();
            if (connected) {
                sendHeartbeat();
            }
        } catch (Exception e) {
            LOG.error("Exception during heartbeat task", e);
        }
    }

    private void sendHeartbeat() {
        heartbeatSender.send();
        LOG.debug("Heartbeat sent.");
    }

    public synchronized void stop() {
        LOG.info("Stopping HeartbeatManager...");
        scheduler.shutdown();

        try {
            if (!scheduler.awaitTermination(3000, TimeUnit.MILLISECONDS)) {
                LOG.warn("Scheduler did not terminate in time. Forcing shutdown...");
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            LOG.error("Interrupted while stopping scheduler...", e);
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

