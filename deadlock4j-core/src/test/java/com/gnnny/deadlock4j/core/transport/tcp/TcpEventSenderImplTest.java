package com.gnnny.deadlock4j.core.transport.tcp;

import com.deadlock4j.proto.MessageProto;
import com.gnnny.deadlock4j.core.event.DeadlockEvent;
import com.gnnny.deadlock4j.core.event.ThreadDeadlockEvent;
import com.gnnny.deadlock4j.util.ProtoConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static org.mockito.Mockito.*;

class TcpEventSenderImplTest {

    private ConnectionManager connectionManager;
    private TcpEventSenderImpl tcpEventSender;
    private Socket socket;
    private OutputStream outputStream;

    @BeforeEach
    void set_up() throws IOException {
        socket = mock(Socket.class);
        outputStream = mock(BufferedOutputStream.class);
        connectionManager = mock(ConnectionManager.class);
        tcpEventSender = new TcpEventSenderImpl(connectionManager);

        // given
        when(connectionManager.getSocket()).thenReturn(socket);
        when(connectionManager.isConnected()).thenReturn(true);
        when(socket.getOutputStream()).thenReturn(outputStream);
        when(socket.isConnected()).thenReturn(true);
    }
    @Test
    void should_attempt_reconnect_when_connection_is_lost() {
        // given
        doReturn(false).when(connectionManager).isConnected();
        doReturn(false).when(connectionManager).connect();

        MessageProto messageProto = createTestMessageProto();

        // when
        tcpEventSender.send(messageProto);

        // then
        verify(connectionManager, times(1)).connect();
    }


    private MessageProto createTestMessageProto() {
        DeadlockEvent event = new ThreadDeadlockEvent("thread-2", 102L, "WAITING");
        return ProtoConverter.toProto(event);
    }
}
