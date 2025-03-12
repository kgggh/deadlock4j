package com.gnnny.deadlock4j.bootstrap;

import com.gnnny.deadlock4j.config.Deadlock4jConfig;
import com.gnnny.deadlock4j.detector.DatabaseDeadlockDetector;
import com.gnnny.deadlock4j.detector.ThreadDeadlockDetector;
import com.gnnny.deadlock4j.exception.DatabaseDeadlockExceptionChecker;
import com.gnnny.deadlock4j.handler.database.DatabaseDeadlockEventSendHandler;
import com.gnnny.deadlock4j.handler.database.DatabaseDeadlockHandlerManager;
import com.gnnny.deadlock4j.handler.database.DatabaseDeadlockLogHandler;
import com.gnnny.deadlock4j.handler.thread.ThreadDeadlockEventSendHandler;
import com.gnnny.deadlock4j.handler.thread.ThreadDeadlockHandlerManager;
import com.gnnny.deadlock4j.handler.thread.ThreadDeadlockLogHandler;
import com.gnnny.deadlock4j.transport.ConnectionManager;
import com.gnnny.deadlock4j.transport.EventSender;
import com.gnnny.deadlock4j.transport.NoOpConnectionManager;
import com.gnnny.deadlock4j.transport.heartbeat.HeartbeatManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Deadlock4jInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(Deadlock4jInitializer.class.getName());
    private final Deadlock4jConfig config;
    private final ScheduledExecutorService deadlockDetectionScheduler;
    private final ThreadDeadlockHandlerManager threadHandlerManager;
    private final DatabaseDeadlockHandlerManager databaseHandlerManager;
    private final HeartbeatManager heartbeatManager;
    private final EventSender eventSender;
    private volatile boolean started = false;

    protected Deadlock4jInitializer(Deadlock4jConfig config,
                                    EventSender eventSender,
                                    ScheduledExecutorService deadlockDetectionScheduler,
                                    HeartbeatManager heartbeatManager) {
        this.config = Objects.requireNonNull(config, "DeadlockBusterConfig must not be null");
        this.eventSender = Objects.requireNonNull(eventSender, "DeadlockEventSendStrategy must not be null");
        this.threadHandlerManager = new ThreadDeadlockHandlerManager(new ThreadDeadlockDetector());
        this.databaseHandlerManager = new DatabaseDeadlockHandlerManager(
            new DatabaseDeadlockDetector(new DatabaseDeadlockExceptionChecker(config.getDetectDatabaseExceptionClasses())));
        this.deadlockDetectionScheduler = deadlockDetectionScheduler;
        this.heartbeatManager = heartbeatManager;
    }

    private static class InstanceHolder {
        private static Deadlock4jInitializer instance;

        static void initialize(Deadlock4jConfig config,
                               EventSender eventSender,
                               ScheduledExecutorService deadlockDetectionScheduler,
                               HeartbeatManager heartbeatManager
                               ) {
            if (instance == null) {
                instance = new Deadlock4jInitializer(config, eventSender, deadlockDetectionScheduler, heartbeatManager);
            }
        }

        static Deadlock4jInitializer getInstance() {
            return instance;
        }
    }

    public static Deadlock4jInitializer getInstance(Deadlock4jConfig config,
                                                    EventSender eventSender,
                                                    ScheduledExecutorService deadlockDetectionScheduler,
                                                    HeartbeatManager heartbeatManager) {
        InstanceHolder.initialize(config, eventSender, deadlockDetectionScheduler, heartbeatManager);

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

        if (config.getTransportType() != null && config.getTransportType() != Deadlock4jConfig.TransportType.NONE) {
            threadHandlerManager.registerHandler(new ThreadDeadlockEventSendHandler(eventSender, config));
            databaseHandlerManager.registerHandler(new DatabaseDeadlockEventSendHandler(eventSender, config));
        }

        if(heartbeatManager != null && !(heartbeatManager.getConnectionManager() instanceof NoOpConnectionManager)) {
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

        deadlockDetectionScheduler.shutdown();
        try {
            if (!deadlockDetectionScheduler.awaitTermination(3000, TimeUnit.MILLISECONDS)) {
                deadlockDetectionScheduler.shutdownNow();
            }

        } catch (InterruptedException e) {
            LOG.error("Interrupted while stopping DeadlockBuster...", e);
            deadlockDetectionScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        if (heartbeatManager != null) {
            heartbeatManager.stop();

            ConnectionManager<?> connectionManager = heartbeatManager.getConnectionManager();
            if (connectionManager != null && connectionManager.isConnected()) {
                connectionManager.close();
            }
        }

        started = false;

        LOG.info("DeadlockBuster stopped...");
    }
}
