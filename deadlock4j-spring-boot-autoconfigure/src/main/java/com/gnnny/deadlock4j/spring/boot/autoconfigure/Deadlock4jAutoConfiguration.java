package com.gnnny.deadlock4j.spring.boot.autoconfigure;

import com.gnnny.deadlock4j.Deadlock4jInitializer;
import com.gnnny.deadlock4j.config.DeadlockBusterConfig;
import com.gnnny.deadlock4j.core.exception.DatabaseDeadlockExceptionChecker;
import com.gnnny.deadlock4j.core.transport.EventSendStrategy;
import com.gnnny.deadlock4j.core.transport.NoOperationEventSendStrategy;
import com.gnnny.deadlock4j.core.transport.queue.QueueEventSendStrategy;
import com.gnnny.deadlock4j.core.transport.queue.QueueEventSender;
import com.gnnny.deadlock4j.core.transport.tcp.ConnectionManager;
import com.gnnny.deadlock4j.core.transport.tcp.TcpEventSendStrategy;
import com.gnnny.deadlock4j.core.transport.tcp.TcpEventSenderImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(Deadlock4jProperties.class)
public class Deadlock4jAutoConfiguration {
    private final Deadlock4jProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public DeadlockBusterConfig deadlockBusterConfig() {
        log.debug("Initializing DeadlockBusterConfig with properties: {}", properties);

        return new DeadlockBusterConfig(
            properties.isLogEnabled(),
            properties.getTcpServerIp(),
            properties.getTcpServerPort(),
            properties.getMonitorInterval(),
            properties.getHeartbeatInterval(),
            properties.getTransportType(),
            properties.getDetectDatabaseExceptionClasses()
        );
    }

    @Bean
    @ConditionalOnMissingBean(name = "deadlockTaskScheduler")
    public ThreadPoolTaskScheduler deadlockTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(4);
        scheduler.setThreadNamePrefix("DLB-Scheduler-");
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    @ConditionalOnMissingBean
    public ScheduledExecutorService deadlockDetectionScheduler(ThreadPoolTaskScheduler deadlockTaskScheduler) {
        return deadlockTaskScheduler.getScheduledExecutor();
    }

    @Bean
    @ConditionalOnMissingBean
    public ScheduledExecutorService heartbeatScheduler(ThreadPoolTaskScheduler deadlockTaskScheduler) {
        return deadlockTaskScheduler.getScheduledExecutor();
    }

    @Bean
    @ConditionalOnMissingBean
    public ScheduledExecutorService connectionManagerScheduler(ThreadPoolTaskScheduler deadlockTaskScheduler) {
        return deadlockTaskScheduler.getScheduledExecutor();
    }

    @Bean
    @ConditionalOnMissingBean
    public QueueEventSender queueEventSender() {
        return event -> log.warn("QueueEventSender is not implemented! Dropping event: {}", event);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectionManager connectionManager(DeadlockBusterConfig config,
                                               ScheduledExecutorService connectionManagerScheduler)
    {
        return new ConnectionManager(config.getTcpServerIp(), config.getTcpServerPort(), connectionManagerScheduler);
    }

    @Bean
    @ConditionalOnMissingBean
    public EventSendStrategy eventSendStrategy(
        DeadlockBusterConfig config,
        QueueEventSender queueEventSender,
        ConnectionManager connectionManager
    ) {
        return switch (config.getTransportType()) {
            case TCP -> {
                log.info("Using TCP event transport.");
                yield new TcpEventSendStrategy(new TcpEventSenderImpl(connectionManager));
            }
            case QUEUE -> {
                log.info("Using Queue event transport.");
                yield new QueueEventSendStrategy(queueEventSender);
            }
            case NONE -> {
                log.info("Event transport is disabled (NONE). Using NoOpEventSendStrategy.");
                yield new NoOperationEventSendStrategy();
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public Deadlock4jInitializer deadlock4jInitializer(
        DeadlockBusterConfig config,
        EventSendStrategy eventSendStrategy,
        ConnectionManager connectionManager,
        ScheduledExecutorService deadlockDetectionScheduler,
        ScheduledExecutorService heartbeatScheduler
    ) {
        return Deadlock4jInitializer.getInstance(config, eventSendStrategy, connectionManager, deadlockDetectionScheduler,
            heartbeatScheduler);
    }

    @Bean
    @ConditionalOnMissingBean
    public Deadlock4jLifecycle deadlockBusterLifecycle(Deadlock4jInitializer deadlock4jInitializer) {
        return new Deadlock4jLifecycle(deadlock4jInitializer);
    }

    @Bean
    @ConditionalOnMissingBean
    public DatabaseDeadlockExceptionChecker databaseDeadlockExceptionChecker(DeadlockBusterConfig config) {
        return new DatabaseDeadlockExceptionChecker(config.getDetectDatabaseExceptionClasses());
    }

    @Bean
    @ConditionalOnMissingBean
    public Deadlock4jDetectAspect deadlockDetectAspect(DatabaseDeadlockExceptionChecker databaseDeadlockExceptionChecker) {
        return new Deadlock4jDetectAspect(databaseDeadlockExceptionChecker);
    }
}
