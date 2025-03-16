package com.github.kgggh.deadlock4j.bootstrap;

import com.github.kgggh.deadlock4j.config.Deadlock4jConfig;
import com.github.kgggh.deadlock4j.handler.database.DatabaseDeadlockEventSendHandler;
import com.github.kgggh.deadlock4j.handler.database.DatabaseDeadlockHandler;
import com.github.kgggh.deadlock4j.handler.database.DatabaseDeadlockHandlerManager;
import com.github.kgggh.deadlock4j.handler.database.DatabaseDeadlockLogHandler;
import com.github.kgggh.deadlock4j.handler.thread.ThreadDeadlockEventSendHandler;
import com.github.kgggh.deadlock4j.handler.thread.ThreadDeadlockHandler;
import com.github.kgggh.deadlock4j.handler.thread.ThreadDeadlockHandlerManager;
import com.github.kgggh.deadlock4j.handler.thread.ThreadDeadlockLogHandler;
import com.github.kgggh.deadlock4j.transport.ConnectionManager;
import com.github.kgggh.deadlock4j.transport.EventSender;
import com.github.kgggh.deadlock4j.transport.NoOpConnectionManager;
import com.github.kgggh.deadlock4j.transport.heartbeat.HeartbeatManager;
import com.github.kgggh.deadlock4j.util.LockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
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
    private final LockManager lockManager;

    private static Deadlock4jInitializer instance;

    private Deadlock4jInitializer(Deadlock4jConfig config,
                                 EventSender eventSender,
                                 ScheduledExecutorService deadlockDetectionScheduler,
                                 HeartbeatManager heartbeatManager,
                                 LockManager lockManager,
                                 ThreadDeadlockHandlerManager threadHandlerManager,
                                 DatabaseDeadlockHandlerManager databaseHandlerManager) {
        this.config = Objects.requireNonNull(config, "DeadlockBusterConfig must not be null");
        this.eventSender = Objects.requireNonNull(eventSender, "DeadlockEventSendStrategy must not be null");
        this.lockManager = lockManager;
        this.threadHandlerManager = threadHandlerManager;
        this.databaseHandlerManager = databaseHandlerManager;
        this.deadlockDetectionScheduler = deadlockDetectionScheduler;
        this.heartbeatManager = heartbeatManager;
    }

    public static Deadlock4jInitializer getInstance(Deadlock4jConfig deadlock4jConfig,
                                                    EventSender eventSender,
                                                    ScheduledExecutorService deadlockDetectionScheduler,
                                                    HeartbeatManager heartbeatManager,
                                                    LockManager lockManager,
                                                    ThreadDeadlockHandlerManager threadHandlerManager,
                                                    DatabaseDeadlockHandlerManager databaseHandlerManager) {
        if (instance == null) {
            instance = new Deadlock4jInitializer(deadlock4jConfig, eventSender, deadlockDetectionScheduler, heartbeatManager,
                lockManager, threadHandlerManager, databaseHandlerManager);
        }

        return instance;
    }

    public synchronized void start() {
        if(!config.isEnabled()) {
            LOG.warn("DeadlockBuster is disabled.");
            return;
        }

        if (started) {
            LOG.warn("DeadlockBuster is already running.");
            return;
        }

        LOG.info("DeadlockBuster is starting...");

        startHeartbeatManager();

        registerThreadHandlers();
        registerDatabaseHandlers();

        startDeadlockHandlerExecutors();

        started = true;

        LOG.info("DeadlockBuster started with monitoring interval: {} ms", config.getMonitorInterval());
    }

    private void startHeartbeatManager() {
        if(heartbeatManager != null && !(heartbeatManager.getConnectionManager() instanceof NoOpConnectionManager)) {
            heartbeatManager.start();
        }
    }

    private void registerDatabaseHandlers() {
        List<DatabaseDeadlockHandler> handlers = getDatabaseDeadlockHandlers();
        handlers.forEach(databaseHandlerManager::registerHandler);
    }

    private List<DatabaseDeadlockHandler> getDatabaseDeadlockHandlers() {
        List<DatabaseDeadlockHandler> handlers = new ArrayList<>();
        if (config.isLogEnabled()) {
            handlers.add(new DatabaseDeadlockLogHandler());
        }
        if (config.getTransportType() != null && config.getTransportType() != Deadlock4jConfig.TransportType.NONE) {
            handlers.add(new DatabaseDeadlockEventSendHandler(eventSender, config));
        }

        return handlers;
    }

    private void registerThreadHandlers() {
        List<ThreadDeadlockHandler> handlers = getThreadDeadlockHandlers();
        handlers.forEach(threadHandlerManager::registerHandler);
    }

    private List<ThreadDeadlockHandler> getThreadDeadlockHandlers() {
        List<ThreadDeadlockHandler> handlers = new ArrayList<>();
        if (config.isLogEnabled()) {
            handlers.add(new ThreadDeadlockLogHandler());
        }
        if (config.getTransportType() != null && config.getTransportType() != Deadlock4jConfig.TransportType.NONE) {
            handlers.add(new ThreadDeadlockEventSendHandler(eventSender, config));
        }

        return handlers;
    }

    private void startDeadlockHandlerExecutors() {
        startThreadHandler();
        startDatabaseHandler();
    }

    private void startDatabaseHandler() {
        deadlockDetectionScheduler.scheduleAtFixedRate(() -> {
            lockManager.lock(LockManager.LockCategory.DATABASE);
            try {
                executeDatabaseHandlers();
            } catch (Exception e) {
                LOG.error("Error executing database deadlock handlers.", e);
            } finally {
                lockManager.unlock(LockManager.LockCategory.DATABASE);
            }
        }, 0, config.getMonitorInterval(), TimeUnit.MILLISECONDS);
    }

    private void startThreadHandler() {
        deadlockDetectionScheduler.scheduleAtFixedRate(() -> {
            lockManager.lock(LockManager.LockCategory.THREAD);
            try {
                executeThreadHandlers();
            } catch (Exception e) {
                LOG.error("Error executing thread deadlock handlers.", e);
            } finally {
                lockManager.unlock(LockManager.LockCategory.THREAD);
            }
        }, 0, config.getMonitorInterval(), TimeUnit.MILLISECONDS);
    }

    private void executeThreadHandlers() {
        try {
            LOG.debug("Executing thread deadlock handlers...");
            threadHandlerManager.processHandlers();
        } catch (Exception e) {
            LOG.error("Error executing thread deadlock handlers.", e);
        }
    }

    private void executeDatabaseHandlers() {
        try {
            LOG.debug("Executing database deadlock handlers...");
            databaseHandlerManager.processHandlers();
        } catch (Exception e) {
            LOG.error("Error executing database deadlock handlers.", e);
        }
    }

    public synchronized void stop() {
        if (!started) {
            LOG.warn("DeadlockBuster is not running.");
            return;
        }

        LOG.info("Stopping DeadlockBuster...");

        threadHandlerManager.stop();
        databaseHandlerManager.stop();

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
