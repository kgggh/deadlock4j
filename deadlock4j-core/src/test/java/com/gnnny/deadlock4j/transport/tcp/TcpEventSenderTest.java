package com.gnnny.deadlock4j.transport.tcp;

import com.gnnny.deadlock4j.event.ThreadDeadlockEvent;
import com.gnnny.deadlock4j.protobuf.ProtoBufConverter;
import com.gnnny.deadlock4j.protobuf.SystemEventProto;
import com.gnnny.deadlock4j.transport.DeadlockEventPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static org.mockito.Mockito.*;

class TcpEventSenderTest {

    private TcpConnectionManager tcpConnectionManager;
    private TcpEventSender tcpEventSender;
    private Socket socket;
    private OutputStream outputStream;

    @BeforeEach
    void set_up() throws IOException {
        socket = mock(Socket.class);
        outputStream = mock(BufferedOutputStream.class);
        tcpConnectionManager = mock(TcpConnectionManager.class);
        tcpEventSender = new TcpEventSender(tcpConnectionManager);

        // given
        when(tcpConnectionManager.getConnection()).thenReturn(socket);
        when(tcpConnectionManager.isConnected()).thenReturn(true);
        when(socket.getOutputStream()).thenReturn(outputStream);
        when(socket.isConnected()).thenReturn(true);
    }
    @Test
    void should_attempt_reconnect_when_connection_is_lost() {
        // given
        doReturn(false).when(tcpConnectionManager).isConnected();
        doReturn(false).when(tcpConnectionManager).connect();

        SystemEventProto systemEventProto = createTestSystemEventProto();

        // when
        tcpEventSender.send(systemEventProto);

        // then
        verify(tcpConnectionManager, times(1)).connect();
    }


    private SystemEventProto createTestSystemEventProto() {
        DeadlockEventPayload deadlockEventPayload = new DeadlockEventPayload(
            "app-1",
            new ThreadDeadlockEvent(
                System.currentTimeMillis(),
                "thread-1",
                101L,
                "BLOCKED",
                5,
                3,
                "java.util.concurrent.locks.ReentrantLock",
                102L,
                "thread-2",
                "stackTrace info..."
            )
        );

        return ProtoBufConverter.convertDeadlockEventToProto(deadlockEventPayload);
    }
}
