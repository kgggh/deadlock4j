package com.gnnny.deadlock4j.core.transport.tcp;

import com.deadlockbuster.proto.MessageProto;
import com.gnnny.deadlock4j.util.ProtoConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

class HeartbeatManagerTest {

    private ConnectionManager connectionManager;
    private HeartbeatManager heartbeatManager;
    private TcpEventSender tcpEventSender;
    private ScheduledExecutorService scheduler;

    @BeforeEach
    void setUp() {
        connectionManager = mock(ConnectionManager.class);
        tcpEventSender = mock(TcpEventSender.class);
        scheduler = mock(ScheduledExecutorService.class);

        heartbeatManager = new HeartbeatManager(1000, tcpEventSender, connectionManager, scheduler);
    }

    @Test
    void should_attempt_connection_when_not_connected() throws InterruptedException {
        // given
        doReturn(false).when(connectionManager).isConnected();
        doReturn(false).when(connectionManager).connect();

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(scheduler).scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any());

        // when
        heartbeatManager.start();
        TimeUnit.MILLISECONDS.sleep(1100);

        // then
        verify(connectionManager, times(1)).connect();
    }


    @Test
    void should_send_heartbeat_when_connected() throws InterruptedException {
        // given
        doReturn(true).when(connectionManager).isConnected();

        MessageProto expected_message = ProtoConverter.createHeartbeatMessage();

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(scheduler).scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any());

        // when
        heartbeatManager.start();
        TimeUnit.MILLISECONDS.sleep(1100);

        // then
        verify(tcpEventSender, times(1)).send(expected_message);
    }

    @Test
    void should_stop_scheduler_on_stop() throws InterruptedException {
        // given
        doNothing().when(scheduler).shutdown();
        doReturn(true).when(scheduler).awaitTermination(5000, TimeUnit.MILLISECONDS);

        // when
        heartbeatManager.stop();

        // then
        verify(scheduler, times(1)).shutdown();
    }
}
