package com.github.kgggh.deadlock4j.transport.heartbeat;

import com.github.kgggh.deadlock4j.config.Deadlock4jConfig;
import com.github.kgggh.deadlock4j.transport.tcp.TcpConnectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

class HeartbeatManagerTest {

    private TcpConnectionManager tcpConnectionManager;
    private HeartbeatManager heartbeatManager;
    private HeartbeatSender heartbeatSender;
    private ScheduledExecutorService scheduler;
    private Deadlock4jConfig config;

    @BeforeEach
    void setUp() {
        tcpConnectionManager = mock(TcpConnectionManager.class);
        heartbeatSender = mock(HeartbeatSender.class);
        scheduler = mock(ScheduledExecutorService.class);
        scheduler = mock(ScheduledExecutorService.class);
        config = mock(Deadlock4jConfig.class);
        when(config.getInstanceId()).thenReturn("instanceId");
        heartbeatManager = new HeartbeatManager(config, heartbeatSender, tcpConnectionManager, scheduler);
    }

    @Test
    void should_attempt_connection_when_not_connected() throws InterruptedException {
        // given
        doReturn(false).when(tcpConnectionManager).isConnected();
        doReturn(false).when(tcpConnectionManager).connect();

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(scheduler).scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any());

        // when
        heartbeatManager.start();
        TimeUnit.MILLISECONDS.sleep(1100);

        // then
        verify(tcpConnectionManager, times(1)).connect();
    }


    @Test
    void should_send_heartbeat_when_connected() throws InterruptedException {
        // given
        doReturn(true).when(tcpConnectionManager).isConnected();

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(scheduler).scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any());

        // when
        heartbeatManager.start();
        TimeUnit.MILLISECONDS.sleep(1100);

        // then
        verify(heartbeatSender, times(1)).send();
    }

    @Test
    void should_stop_scheduler_on_stop() throws InterruptedException {
        // given
        doNothing().when(scheduler).shutdown();
        doReturn(true).when(scheduler).awaitTermination(3000, TimeUnit.MILLISECONDS);

        // when
        heartbeatManager.stop();

        // then
        verify(scheduler, times(1)).shutdown();
    }
}
