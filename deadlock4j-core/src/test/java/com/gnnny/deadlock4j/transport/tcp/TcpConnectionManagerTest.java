package com.gnnny.deadlock4j.transport.tcp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TcpConnectionManagerTest {

    private TcpConnectionManager tcpConnectionManager;
    private Socket socket;

    @BeforeEach
    void set_up() {
        tcpConnectionManager = spy(new TcpConnectionManager("127.0.0.1", 8282));
        socket = mock(Socket.class);
    }

    @Test
    void should_return_false_when_connection_fails() throws IOException {
        // given
        doThrow(new IOException("Connection failed")).when(socket).connect(any(InetSocketAddress.class), anyInt());
        doReturn(socket).when(tcpConnectionManager).getConnection();

        // when
        boolean result = tcpConnectionManager.connect();

        // then
        assertThat(result).isFalse();
    }

    @Test
    void should_return_true_when_already_connected() {
        // given
        doReturn(true).when(tcpConnectionManager).isConnected();

        // when
        boolean result = tcpConnectionManager.connect();

        // then
        assertThat(result).isTrue();
    }

    @Test
    void should_schedule_reconnect_when_connection_fails() {
        // given
        TcpConnectionManager spyTcpConnectionManager = spy(new TcpConnectionManager("127.0.0.1", 8282));

        doReturn(false).when(spyTcpConnectionManager).isConnected();
        doCallRealMethod().when(spyTcpConnectionManager).connect();

        // when
        spyTcpConnectionManager.connect();

        // then
    }

    @Test
    void should_return_false_when_socket_is_null_or_closed() {
        // given
        doReturn(null).when(tcpConnectionManager).getConnection();

        // when
        boolean result = tcpConnectionManager.isConnected();

        // then
        assertThat(result).isFalse();
    }

    @Test
    void should_return_null_socket_when_not_connected() {
        // given
        doReturn(false).when(tcpConnectionManager).isConnected();

        // when
        Socket result = tcpConnectionManager.getConnection();

        // then
        assertThat(result).isNull();
    }
}
