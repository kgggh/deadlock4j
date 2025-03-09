package com.gnnny.deadlock4j.core.transport.tcp;

import com.deadlock4j.proto.MessageProto;
import com.gnnny.deadlock4j.util.ProtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartbeatManager {
    private static final Logger LOG = LoggerFactory.getLogger(HeartbeatManager.class);
    private final int heartBeatInterval;
    private final TcpEventSender tcpEventSender;
    private final ConnectionManager connectionManager;
    private final ScheduledExecutorService scheduler; // 🔥 외부에서 주입받도록 변경

    public HeartbeatManager(int heartBeatInterval, TcpEventSender tcpEventSender, ConnectionManager connectionManager, ScheduledExecutorService scheduler) {
        if (heartBeatInterval < 500) {
            throw new IllegalArgumentException("Heartbeat interval must be greater than 500. Given: " + heartBeatInterval);
        }

        this.heartBeatInterval = heartBeatInterval;
        this.tcpEventSender = tcpEventSender;
        this.connectionManager = connectionManager;
        this.scheduler = scheduler;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public synchronized void start() {
        if (scheduler.isShutdown()) {
            LOG.warn("Scheduler is already shutdown. Cannot start HeartbeatManager.");
            return;
        }

        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (!connectionManager.isConnected()) {
                    LOG.warn("No active connection. Attempting to reconnect...");
                    connectionManager.connect();
                }
                sendHeartbeat();
            } catch (Exception e) {
                LOG.error("Failed to send heartbeat", e);
            }
        }, 0, heartBeatInterval, TimeUnit.MILLISECONDS);
    }

    private void sendHeartbeat() {
        MessageProto heartbeatMessage = ProtoConverter.createHeartbeatMessage();
        tcpEventSender.send(heartbeatMessage);
        LOG.debug("Heartbeat sent.");
    }

    public synchronized void stop() {
        LOG.info("Stopping HeartbeatManager...");
        scheduler.shutdown();

        try {
            if (!scheduler.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
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

