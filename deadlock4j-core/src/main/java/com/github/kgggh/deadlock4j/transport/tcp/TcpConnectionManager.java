package com.github.kgggh.deadlock4j.transport.tcp;

import com.github.kgggh.deadlock4j.transport.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;

public class TcpConnectionManager implements ConnectionManager<Socket> {
    private static final Logger LOG = LoggerFactory.getLogger(TcpConnectionManager.class);
    private final String serverHost;
    private final int serverPort;
    private Socket socket;

    public TcpConnectionManager(String serverHost, int serverPort) {
        this.serverHost = Objects.requireNonNull(serverHost);
        this.serverPort = serverPort;
    }

    @Override
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

            return true;
        } catch (IOException e) {
            LOG.warn("Failed to connect to server", e);
            return false;
        }
    }

    @Override
    public synchronized boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    @Override
    public synchronized void close() {
        LOG.info("Closing connection...");
        try {
            if (!socket.isClosed()) {
                socket.close();
                LOG.debug("Connection closed.");
            }
        } catch (IOException e) {
            LOG.error("Error closing connection", e);
        } finally {
            socket = null;
        }
    }

    @Override
    public synchronized Socket getConnection() {
        return socket;
    }
}
