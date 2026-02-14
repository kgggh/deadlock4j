package com.github.kgggh.deadlock4j.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class SchedulerUtils {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerUtils.class);

    private SchedulerUtils() {
    }

    public static void shutdownGracefully(ScheduledExecutorService scheduler, long timeout, TimeUnit unit) {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(timeout, unit)) {
                LOG.warn("Scheduler did not terminate in time. Forcing shutdown...");
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            LOG.error("Interrupted while stopping scheduler...", e);
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
