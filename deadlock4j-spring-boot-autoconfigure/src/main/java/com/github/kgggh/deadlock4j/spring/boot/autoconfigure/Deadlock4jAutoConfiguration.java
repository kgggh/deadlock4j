package com.github.kgggh.deadlock4j.spring.boot.autoconfigure;

import com.github.kgggh.deadlock4j.bootstrap.Deadlock4jInitializer;
import com.github.kgggh.deadlock4j.config.Deadlock4jConfig;
import com.github.kgggh.deadlock4j.detector.DatabaseDeadlockDetector;
import com.github.kgggh.deadlock4j.detector.ThreadDeadlockDetector;
import com.github.kgggh.deadlock4j.exception.DatabaseDeadlockExceptionChecker;
import com.github.kgggh.deadlock4j.handler.database.DatabaseDeadlockHandlerManager;
import com.github.kgggh.deadlock4j.handler.thread.ThreadDeadlockHandlerManager;
import com.github.kgggh.deadlock4j.transport.ConnectionManager;
import com.github.kgggh.deadlock4j.transport.EventSender;
import com.github.kgggh.deadlock4j.transport.NoOpConnectionManager;
import com.github.kgggh.deadlock4j.transport.NoOpEventSender;
import com.github.kgggh.deadlock4j.transport.heartbeat.HeartbeatManager;
import com.github.kgggh.deadlock4j.transport.heartbeat.HeartbeatSender;
import com.github.kgggh.deadlock4j.transport.heartbeat.NoOpHeartbeatSender;
import com.github.kgggh.deadlock4j.transport.heartbeat.TcpHeartbeatSender;
import com.github.kgggh.deadlock4j.transport.queue.QueueEventSender;
import com.github.kgggh.deadlock4j.transport.tcp.TcpConnectionManager;
import com.github.kgggh.deadlock4j.transport.tcp.TcpEventSender;
import com.github.kgggh.deadlock4j.util.LockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(prefix = "deadlock4j", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(Deadlock4jProperties.class)
public class Deadlock4jAutoConfiguration {
    private final Deadlock4jProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public Deadlock4jConfig deadlock4jConfig() {
        log.debug("Initializing DeadlockBusterConfig with properties: {}", properties);

        return new Deadlock4jConfig(
            properties.isEnabled(),
            properties.isLogEnabled(),
            properties.getInstanceId(),
            properties.getTcpServerIp(),
            properties.getTcpServerPort(),
            properties.getMonitorInterval(),
            properties.getHeartbeatInterval(),
            properties.getTransportType()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectionManager<?> connectionManager(Deadlock4jConfig deadlock4jConfig) {
        if(deadlock4jConfig.getTransportType() != Deadlock4jConfig.TransportType.TCP) {
            return new NoOpConnectionManager();
        }

        return new TcpConnectionManager(deadlock4jConfig.getTcpServerIp(), deadlock4jConfig.getTcpServerPort());
    }

    @Bean
    @ConditionalOnMissingBean
    public EventSender eventSender(ConnectionManager<?> connectionManager) {
        return switch (properties.getTransportType()) {
            case TCP ->  new TcpEventSender((TcpConnectionManager) connectionManager);
            case QUEUE -> new QueueEventSender();
            case NONE -> new NoOpEventSender();
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public HeartbeatSender heartbeatSender(EventSender eventSender, Deadlock4jConfig deadlock4jConfig) {
        if (Objects.requireNonNull(deadlock4jConfig.getTransportType()) == Deadlock4jConfig.TransportType.TCP) {
            return new TcpHeartbeatSender((TcpEventSender) eventSender, deadlock4jConfig);
        }

        return new NoOpHeartbeatSender();
    }

    @Bean
    @ConditionalOnMissingBean
    public HeartbeatManager heartbeatManager(ConnectionManager<?> connectionManager,
                                             HeartbeatSender heartbeatSender,
                                             Deadlock4jConfig deadlock4jConfig) {
        ScheduledExecutorService heartbeatScheduler = Executors.newScheduledThreadPool(1);
        return new HeartbeatManager(
            deadlock4jConfig,
            heartbeatSender,
            connectionManager,
            heartbeatScheduler
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public ThreadDeadlockHandlerManager threadDeadlockHandlerManager() {
        return new ThreadDeadlockHandlerManager(new ThreadDeadlockDetector());
    }

    @Bean
    @ConditionalOnMissingBean
    public DatabaseDeadlockHandlerManager databaseDeadlockHandlerManager(DatabaseDeadlockExceptionChecker databaseDeadlockExceptionChecker) {
        return new DatabaseDeadlockHandlerManager(new DatabaseDeadlockDetector(databaseDeadlockExceptionChecker));
    }

    @Bean
    @ConditionalOnMissingBean
    public Deadlock4jInitializer deadlock4jInitializer(Deadlock4jConfig deadlock4jConfig,
                                                       EventSender eventSender,
                                                       HeartbeatManager heartbeatManager,
                                                       ThreadDeadlockHandlerManager threadDeadlockHandlerManager,
                                                       DatabaseDeadlockHandlerManager databaseDeadlockHandlerManager) {
        ScheduledExecutorService deadlockDetectionScheduler = Executors.newScheduledThreadPool(2);
        LockManager lockManager = new LockManager();
        return Deadlock4jInitializer.getInstance(deadlock4jConfig, eventSender, deadlockDetectionScheduler, heartbeatManager,
            lockManager, threadDeadlockHandlerManager, databaseDeadlockHandlerManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public Deadlock4jLifecycle deadlockBusterLifecycle(Deadlock4jInitializer deadlock4jInitializer) {
        return new Deadlock4jLifecycle(deadlock4jInitializer);
    }

    @Bean
    @ConditionalOnMissingBean
    public DatabaseDeadlockExceptionChecker databaseDeadlockExceptionChecker() {
        return new DatabaseDeadlockExceptionChecker();
    }

    @Bean
    @ConditionalOnMissingBean
    public Deadlock4jAspect deadlockAspect(DatabaseDeadlockExceptionChecker databaseDeadlockExceptionChecker) {
        return new Deadlock4jAspect(databaseDeadlockExceptionChecker);
    }
}
