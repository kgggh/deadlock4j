package com.gnnny.deadlock4j.core.transport.tcp;

import com.deadlockbuster.proto.MessageProto;
import com.gnnny.deadlock4j.core.event.DeadlockEvent;
import com.gnnny.deadlock4j.util.ProtoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class TcpEventSenderImpl implements TcpEventSender {
    private static final Logger LOG = LoggerFactory.getLogger(TcpEventSenderImpl.class);
    private final ConnectionManager connectionManager;
    private OutputStream outputStream;
    private Socket lastSocket;

    public TcpEventSenderImpl(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void send(DeadlockEvent event) {
        MessageProto message = ProtoConverter.toProto(event);
        sendMessage(message);
    }

    @Override
    public void send(MessageProto message) {
        sendMessage(message);
    }

    private void sendMessage(MessageProto message) {
        byte[] data = message.toByteArray();
        LOG.debug("Sending message size: {}, message: {}", data.length, message);
        sendData(data);
    }

    private void sendData(byte[] data) {
        try {
            ensureConnection();
            if (outputStream != null) {
                outputStream.write(data);
                outputStream.flush();
                LOG.debug("Message successfully sent.");
            } else {
                LOG.warn("OutputStream is null. Failed to send data.");
            }
        } catch (IOException e) {
            LOG.error("Failed to send data", e);
            if (e instanceof SocketException || e.getMessage().contains("Broken pipe")) {
                LOG.warn("Detected broken pipe. Closing connection and reconnecting...");
                handleReconnect();
            }
        }
    }

    private void ensureConnection() throws IOException {
        if (!connectionManager.isConnected()) {
            LOG.warn("Connection is not active. Attempting to reconnect...");
            connectionManager.connect();
        }

        Socket socket = connectionManager.getSocket();
        if (socket != null && socket.isConnected()) {
            if (outputStream == null || socket != lastSocket || socket.isClosed()) {
                LOG.info("Creating new OutputStream...");
                closeOutputStream();
                outputStream = new BufferedOutputStream(socket.getOutputStream());
                lastSocket = socket;
            }
        } else {
            LOG.error("Failed to establish connection. Socket is null or closed.");
        }
    }

    private void closeOutputStream() {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                LOG.warn("Failed to close output stream", e);
            }
        }
    }

    private void handleReconnect() {
        closeOutputStream();
        connectionManager.shutdown();
        connectionManager.connect();
    }
}
