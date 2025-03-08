package com.gnnny.deadlock4j.core.transport.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConnectionManager {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManager.class);
    private final String serverHost;
    private final int serverPort;
    private Socket socket;
    private final ScheduledExecutorService scheduler;
    private volatile boolean reconnecting = false;

    public ConnectionManager(String serverHost, int serverPort, ScheduledExecutorService scheduler) {
        this.serverHost = Objects.requireNonNull(serverHost);
        this.serverPort = serverPort;
        this.scheduler = scheduler;
    }

    public synchronized boolean connect() {
        if (isConnected()) {
            LOG.debug("Already connected to server.");
            return true;
        }

        try {
            socket = new Socket();
            socket.setKeepAlive(true);
            socket.connect(new InetSocketAddress(serverHost, serverPort), 5000);
            LOG.info("Connected to server: {}:{}", serverHost, serverPort);

            reconnecting = false;
            return true;
        } catch (IOException e) {
            LOG.warn("Failed to connect to server", e);
            scheduleReconnect();
            return false;
        }
    }

    private void scheduleReconnect() {
        if (reconnecting || scheduler.isShutdown()) {
            LOG.warn("Reconnect scheduler is already running or shutdown. Skipping...");
            return;
        }

        reconnecting = true;
        scheduler.scheduleAtFixedRate(() -> {
            if (!isConnected()) {
                LOG.warn("Attempting to reconnect...");
                connect();
            } else {
                LOG.info("Reconnection successful. Stopping reconnect attempts.");
                reconnecting = false;
            }
        }, 0, 5000, TimeUnit.MILLISECONDS);
    }

    public synchronized boolean isConnected() {
        if (socket == null || socket.isClosed()) {
            return false;
        }

        try {
            return socket.isConnected() && !socket.isClosed();
        } catch (Exception e) {
            LOG.warn("Connection check failed, assuming disconnected.");
            return false;
        }
    }

    public synchronized void close() {
        LOG.info("Closing connection...");
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                LOG.debug("Connection closed.");
            }
        } catch (IOException e) {
            LOG.error("Error closing connection", e);
        }
    }

    public synchronized Socket getSocket() {
        if (!isConnected()) {
            LOG.warn("Socket is not connected. Returning null.");
            return null;
        }
        return socket;
    }

    public void shutdown() {
        LOG.info("Shutting down ConnectionManager...");
        close();

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
