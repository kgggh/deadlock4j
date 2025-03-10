package com.gnnny.deadlock4j;

import com.gnnny.deadlock4j.config.DeadlockBusterConfig;
import com.gnnny.deadlock4j.core.detector.DatabaseDeadlockDetector;
import com.gnnny.deadlock4j.core.detector.ThreadDeadlockDetector;
import com.gnnny.deadlock4j.core.exception.DatabaseDeadlockExceptionChecker;
import com.gnnny.deadlock4j.core.handler.database.DatabaseDeadlockEventSendHandler;
import com.gnnny.deadlock4j.core.handler.database.DatabaseDeadlockHandlerManager;
import com.gnnny.deadlock4j.core.handler.database.DatabaseDeadlockLogHandler;
import com.gnnny.deadlock4j.core.handler.thread.ThreadDeadlockEventSendHandler;
import com.gnnny.deadlock4j.core.handler.thread.ThreadDeadlockHandlerManager;
import com.gnnny.deadlock4j.core.handler.thread.ThreadDeadlockLogHandler;
import com.gnnny.deadlock4j.core.transport.EventSendStrategy;
import com.gnnny.deadlock4j.core.transport.tcp.ConnectionManager;
import com.gnnny.deadlock4j.core.transport.tcp.HeartbeatManager;
import com.gnnny.deadlock4j.core.transport.tcp.TcpEventSendStrategy;
import com.gnnny.deadlock4j.core.transport.tcp.TcpEventSenderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Deadlock4jInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(Deadlock4jInitializer.class.getName());
    private final DeadlockBusterConfig config;
    private final ScheduledExecutorService deadlockDetectionScheduler;
    private final ThreadDeadlockHandlerManager threadHandlerManager;
    private final DatabaseDeadlockHandlerManager databaseHandlerManager;
    private HeartbeatManager heartbeatManager;
    private final EventSendStrategy eventSendStrategy;
    private volatile boolean started = false;

    protected Deadlock4jInitializer(DeadlockBusterConfig config, EventSendStrategy eventSendStrategy, ConnectionManager connectionManager, ScheduledExecutorService deadlockDetectionScheduler, ScheduledExecutorService heartbeatScheduler) {
        this.config = Objects.requireNonNull(config, "DeadlockBusterConfig must not be null");
        this.eventSendStrategy = Objects.requireNonNull(eventSendStrategy, "DeadlockEventSendStrategy must not be null");
        this.threadHandlerManager = new ThreadDeadlockHandlerManager(new ThreadDeadlockDetector());
        this.databaseHandlerManager = new DatabaseDeadlockHandlerManager(
            new DatabaseDeadlockDetector(new DatabaseDeadlockExceptionChecker(config.getDetectDatabaseExceptionClasses())));
        this.deadlockDetectionScheduler = deadlockDetectionScheduler;

        if(config.getTransportType() == DeadlockBusterConfig.TransportType.TCP) {
            this.heartbeatManager = new HeartbeatManager(
                config.getHeartbeatInterval(),
                new TcpEventSenderImpl(connectionManager),
                connectionManager,
                heartbeatScheduler
            );
        }
    }

    private static class InstanceHolder {
        private static Deadlock4jInitializer instance;

        static void initialize(DeadlockBusterConfig config, EventSendStrategy eventSendStrategy, ConnectionManager connectionManager, ScheduledExecutorService deadlockDetectionScheduler, ScheduledExecutorService heartbeatScheduler) {
            if (instance == null) {
                instance = new Deadlock4jInitializer(config, eventSendStrategy, connectionManager, deadlockDetectionScheduler, heartbeatScheduler);
            }
        }

        static Deadlock4jInitializer getInstance() {
            return instance;
        }
    }

    public static Deadlock4jInitializer getInstance(DeadlockBusterConfig config, EventSendStrategy eventSendStrategy, ConnectionManager connectionManager, ScheduledExecutorService deadlockDetectionScheduler, ScheduledExecutorService heartbeatScheduler) {
        InstanceHolder.initialize(config, eventSendStrategy, connectionManager, deadlockDetectionScheduler, heartbeatScheduler);

        return InstanceHolder.getInstance();
    }

    public synchronized void start() {
        if (started) {
            LOG.warn("DeadlockBuster is already running.");
            return;
        }

        long monitoringInterval = config.getMonitorInterval();
        if (monitoringInterval < 500) {
            throw new IllegalArgumentException("monitoringInterval interval must be greater than 500. Given: " + monitoringInterval);
        }


        LOG.info("DeadlockBuster is starting...");

        if (config.isLogEnabled()) {
            threadHandlerManager.registerHandler(new ThreadDeadlockLogHandler());
            databaseHandlerManager.registerHandler(new DatabaseDeadlockLogHandler());
        }

        if (config.getTransportType() != null || config.getTransportType() != DeadlockBusterConfig.TransportType.NONE) {
            threadHandlerManager.registerHandler(new ThreadDeadlockEventSendHandler(eventSendStrategy));
            databaseHandlerManager.registerHandler(new DatabaseDeadlockEventSendHandler(eventSendStrategy));
        }

        if(heartbeatManager != null) {
            heartbeatManager.start();
        }

        deadlockDetectionScheduler.scheduleWithFixedDelay(() -> {
            try {
                executeHandlers();
            } catch (Exception e) {
                LOG.error("Unexpected error in DeadlockBuster execution", e);
            }
        }, 0, monitoringInterval, TimeUnit.MILLISECONDS);

        started = true;

        LOG.info("DeadlockBuster started with monitoring interval: {} ms", monitoringInterval);
    }

    private void executeHandlers() {
        try {
            LOG.debug("Executing registered handlers...");
            threadHandlerManager.executeHandlers();
            databaseHandlerManager.executeHandlers();
        } catch (Exception e) {
            LOG.error("Error executing handlers..", e);
        }
    }

    public synchronized void stop() {
        if (!started) {
            LOG.warn("DeadlockBuster is not running.");
            return;
        }

        LOG.info("Stopping DeadlockBuster...");

        if (heartbeatManager != null) {
            LOG.info("Stopping HeartbeatManager...");
            heartbeatManager.stop();

            ConnectionManager connectionManager = heartbeatManager.getConnectionManager();
            if (connectionManager != null && eventSendStrategy instanceof TcpEventSendStrategy) {
                LOG.info("Shutting down ConnectionManager...");
                connectionManager.shutdown();
            }
        }

        deadlockDetectionScheduler.shutdown();

        try {
            if (!deadlockDetectionScheduler.awaitTermination(3000, TimeUnit.MILLISECONDS)) {
                List<Runnable> droppedTasks = deadlockDetectionScheduler.shutdownNow();
                LOG.warn("DeadlockBuster forced shutdown. {} tasks were dropped.", droppedTasks.size());
            }

        } catch (InterruptedException e) {
            LOG.error("Interrupted while stopping DeadlockBuster...", e);
            deadlockDetectionScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        started = false;

        LOG.info("DeadlockBuster has been stopped.");
    }
}


