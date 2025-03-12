package com.gnnny.deadlock4j.spring.boot.autoconfigure;

import com.gnnny.deadlock4j.bootstrap.Deadlock4jInitializer;
import com.gnnny.deadlock4j.config.Deadlock4jConfig;
import com.gnnny.deadlock4j.exception.DatabaseDeadlockExceptionChecker;
import com.gnnny.deadlock4j.transport.ConnectionManager;
import com.gnnny.deadlock4j.transport.EventSender;
import com.gnnny.deadlock4j.transport.NoOpConnectionManager;
import com.gnnny.deadlock4j.transport.NoOpEventSender;
import com.gnnny.deadlock4j.transport.heartbeat.HeartbeatManager;
import com.gnnny.deadlock4j.transport.heartbeat.HeartbeatSender;
import com.gnnny.deadlock4j.transport.heartbeat.NoOpHeartbeatSender;
import com.gnnny.deadlock4j.transport.heartbeat.TcpHeartbeatSender;
import com.gnnny.deadlock4j.transport.queue.QueueEventSender;
import com.gnnny.deadlock4j.transport.tcp.TcpConnectionManager;
import com.gnnny.deadlock4j.transport.tcp.TcpEventSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(annotation = EnableDeadlock4j.class)
@EnableConfigurationProperties(Deadlock4jProperties.class)
public class Deadlock4jAutoConfiguration {
    private final Deadlock4jProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public Deadlock4jConfig deadlockBusterConfig() {
        log.debug("Initializing DeadlockBusterConfig with properties: {}", properties);

        return new Deadlock4jConfig(
            properties.isLogEnabled(),
            properties.getInstanceId(),
            properties.getTcpServerIp(),
            properties.getTcpServerPort(),
            properties.getMonitorInterval(),
            properties.getHeartbeatInterval(),
            properties.getTransportType(),
            properties.getDetectDatabaseExceptionClasses()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public ScheduledExecutorService deadlockDetectionScheduler() {
        return Executors.newScheduledThreadPool(2);
    }

    @Bean
    @ConditionalOnMissingBean
    public ScheduledExecutorService heartbeatScheduler() {
        return Executors.newScheduledThreadPool(1);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectionManager<?> connectionManager(Deadlock4jConfig config) {
        if(config.getTransportType() != Deadlock4jConfig.TransportType.TCP) {
            return new NoOpConnectionManager();
        }

        return new TcpConnectionManager(properties.getTcpServerIp(), properties.getTcpServerPort());
    }

    @Bean
    @ConditionalOnMissingBean
    public EventSender eventSender(Deadlock4jConfig config, ConnectionManager<?> connectionManager) {
        return switch (config.getTransportType()) {
            case TCP ->  new TcpEventSender((TcpConnectionManager) connectionManager);
            case QUEUE -> new QueueEventSender();
            case NONE -> new NoOpEventSender();
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public HeartbeatSender heartbeatSender(EventSender eventSender, Deadlock4jConfig config) {
        if (Objects.requireNonNull(config.getTransportType()) == Deadlock4jConfig.TransportType.TCP) {
            return new TcpHeartbeatSender((TcpEventSender) eventSender, config);
        }

        return new NoOpHeartbeatSender();
    }

    @Bean
    @ConditionalOnMissingBean
    public HeartbeatManager heartbeatManager(Deadlock4jConfig config,
                                             ConnectionManager<?> connectionManager,
                                             ScheduledExecutorService heartbeatScheduler,
                                             HeartbeatSender heartbeatSender) {
        return new HeartbeatManager(
            config.getHeartbeatInterval(),
            heartbeatSender,
            connectionManager,
            heartbeatScheduler
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public Deadlock4jInitializer deadlock4jInitializer(Deadlock4jConfig config, EventSender eventSender,
                                                       ScheduledExecutorService deadlockDetectionScheduler,
                                                       HeartbeatManager heartbeatManager) {
        return Deadlock4jInitializer.getInstance(config, eventSender, deadlockDetectionScheduler, heartbeatManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public Deadlock4jLifecycle deadlockBusterLifecycle(Deadlock4jInitializer deadlock4jInitializer) {
        return new Deadlock4jLifecycle(deadlock4jInitializer);
    }

    @Bean
    @ConditionalOnMissingBean
    public DatabaseDeadlockExceptionChecker databaseDeadlockExceptionChecker(Deadlock4jConfig config) {
        return new DatabaseDeadlockExceptionChecker(config.getDetectDatabaseExceptionClasses());
    }

    @Bean
    @ConditionalOnMissingBean
    public Deadlock4jDetectAspect deadlockDetectAspect(DatabaseDeadlockExceptionChecker databaseDeadlockExceptionChecker) {
        return new Deadlock4jDetectAspect(databaseDeadlockExceptionChecker);
    }
}
