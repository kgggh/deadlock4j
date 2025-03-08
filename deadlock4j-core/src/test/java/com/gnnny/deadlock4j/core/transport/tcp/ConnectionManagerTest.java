package com.gnnny.deadlock4j.core.transport.tcp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ConnectionManagerTest {

    private ConnectionManager connectionManager;
    private ScheduledExecutorService scheduler;
    private Socket socket;

    @BeforeEach
    void set_up() {
        scheduler = mock(ScheduledExecutorService.class);
        connectionManager = spy(new ConnectionManager("127.0.0.1", 8282, scheduler));
        socket = mock(Socket.class);
    }

    @Test
    void should_return_false_when_connection_fails() throws IOException {
        // given
        doThrow(new IOException("Connection failed")).when(socket).connect(any(InetSocketAddress.class), anyInt());
        doReturn(socket).when(connectionManager).getSocket();

        // when
        boolean result = connectionManager.connect();

        // then
        assertThat(result).isFalse();
    }

    @Test
    void should_return_true_when_already_connected() {
        // given
        doReturn(true).when(connectionManager).isConnected();

        // when
        boolean result = connectionManager.connect();

        // then
        assertThat(result).isTrue();
    }

    @Test
    void should_schedule_reconnect_when_connection_fails() {
        // given
        ConnectionManager spyConnectionManager = spy(new ConnectionManager("127.0.0.1", 8282, scheduler));

        doReturn(false).when(spyConnectionManager).isConnected();
        doCallRealMethod().when(spyConnectionManager).connect();

        // when
        spyConnectionManager.connect();

        // then
        verify(scheduler, timeout(500).times(1)).scheduleAtFixedRate(
            any(Runnable.class), eq(0L), eq(5000L), eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    void should_return_false_when_socket_is_null_or_closed() {
        // given
        doReturn(null).when(connectionManager).getSocket();

        // when
        boolean result = connectionManager.isConnected();

        // then
        assertThat(result).isFalse();
    }

    @Test
    void should_return_null_socket_when_not_connected() {
        // given
        doReturn(false).when(connectionManager).isConnected();

        // when
        Socket result = connectionManager.getSocket();

        // then
        assertThat(result).isNull();
    }

    @Test
    void should_shutdown_properly() throws InterruptedException {
        // given
        doNothing().when(scheduler).shutdown();
        doReturn(true).when(scheduler).awaitTermination(3000, TimeUnit.MILLISECONDS);

        // when
        connectionManager.shutdown();

        // then
        verify(scheduler, times(1)).shutdown();
    }
}
