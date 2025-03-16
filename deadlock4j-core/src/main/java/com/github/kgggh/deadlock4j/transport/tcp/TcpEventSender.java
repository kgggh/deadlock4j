package com.github.kgggh.deadlock4j.transport.tcp;

import com.github.kgggh.deadlock4j.protobuf.ProtoBufConverter;
import com.github.kgggh.deadlock4j.protobuf.SystemEventProto;
import com.github.kgggh.deadlock4j.transport.ConnectionManager;
import com.github.kgggh.deadlock4j.transport.DeadlockEventPayload;
import com.github.kgggh.deadlock4j.transport.EventSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class TcpEventSender implements EventSender {
    private static final Logger LOG = LoggerFactory.getLogger(TcpEventSender.class);
    private final ConnectionManager<Socket> connectionManager;
    private OutputStream outputStream;
    private Socket lastSocket;

    public TcpEventSender(TcpConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void send(DeadlockEventPayload eventPayload) {
        SystemEventProto message = ProtoBufConverter.convertDeadlockEventToProto(eventPayload);
        send(message);
    }

    public void send(SystemEventProto message) {
        byte[] data = message.toByteArray();
        LOG.debug("Sending message size: {}, message: {}", data.length, message);
        sendData(data);
    }

    private void sendData(byte[] data) {
        try {
            ensureConnection();
            if (outputStream != null) {
                outputStream.write(intToBytes(data.length));
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

    private byte[] intToBytes(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    private void ensureConnection() throws IOException {
        if (!connectionManager.isConnected() && !connectionManager.connect()) {
            LOG.error("Failed to establish connection. Skipping message send.");
            return;
        }

        Socket socket = connectionManager.getConnection();
        if (socket == null || !socket.isConnected() || socket.isClosed()) {
            LOG.error("Connection is closed or unavailable.");
            return;
        }

        if (outputStream == null || socket != lastSocket) {
            LOG.debug("Creating a new OutputStream...");
            closeOutputStream();
            outputStream = new BufferedOutputStream(socket.getOutputStream());
            lastSocket = socket;
        }
    }

    private void closeOutputStream() {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                LOG.warn("Failed to close output stream", e);
            } finally {
                outputStream = null;
                lastSocket = null;
            }
        }
    }

    private void handleReconnect() {
        closeOutputStream();
        connectionManager.close();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        connectionManager.connect();
    }
}
