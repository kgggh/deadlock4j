package com.github.kgggh.deadlock4j.detector;

import com.github.kgggh.deadlock4j.event.ThreadDeadlockEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ThreadDeadlockDetector implements DeadlockDetector<ThreadDeadlockEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadDeadlockDetector.class);

    private final ThreadMXBean threadMXBean;
    private final Map<Long, ThreadDeadlockInfo> detectedDeadlockThreads = new ConcurrentHashMap<>();

    private static final long DEADLOCK_WARNING_TIME = (long) 10 * 60 * 1000;
    private static final long DEADLOCK_LOG_INTERVAL = (long) 60 * 1000;
    private static final long DEADLOCK_EVENT_SUPPRESSION_INTERVAL = (long) 5 * 60 * 1000;

    public ThreadDeadlockDetector() {
        this.threadMXBean = ManagementFactory.getThreadMXBean();
    }

    @Override
    public List<ThreadDeadlockEvent> detect() {
        long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
        long currentTime = System.currentTimeMillis();

        if (deadlockedThreads == null) {
            detectedDeadlockThreads.clear();
            return Collections.emptyList();
        }

        List<ThreadDeadlockEvent> detectedEvents = new ArrayList<>();
        Set<Long> activeThreadDeadlocks = new HashSet<>();

        for (long threadId : deadlockedThreads) {
            activeThreadDeadlocks.add(threadId);
            ThreadDeadlockInfo threadDeadlockInfo =
                detectedDeadlockThreads.computeIfAbsent(threadId, id -> new ThreadDeadlockInfo(currentTime));

            logDeadlock(threadId, threadDeadlockInfo, currentTime);

            ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId, Integer.MAX_VALUE);
            if (threadInfo != null && hasSuppressionExpired(threadDeadlockInfo, currentTime)) {
                detectedEvents.add(convertToEvent(threadInfo, threadDeadlockInfo.getDetectedTime()));
                threadDeadlockInfo.setLastEventTime(currentTime);
            }
        }

        removeClearedDeadlocks(activeThreadDeadlocks);
        return detectedEvents;
    }

    private void logDeadlock(long threadId, ThreadDeadlockInfo threadDeadlockInfo, long currentTime) {
        if (threadDeadlockInfo.getLastLoggedTime() == 0) {
            LOG.debug("New Thread deadlock detected. Thread ID: {}", threadId);
            threadDeadlockInfo.setLastLoggedTime(currentTime);
        }

        if (isTimeToLogWarning(threadDeadlockInfo, currentTime)) {
            LOG.warn("Deadlock detected for over 10 minutes. Thread ID: {}", threadId);
            threadDeadlockInfo.setLastLoggedTime(currentTime);
        }
    }

    /**
     * Check it is time to log warning for deadlock that running long time.
     *
     * Warning log will write when:
     * 1. Deadlock is running more than DEADLOCK_WARNING_TIME (10 minutes).
     * 2. Last warning log was written more than DEADLOCK_LOG_INTERVAL (1 minute).
     *
     * This is for prevent too many log and make sure deadlock log is not missing.
     */
    private boolean isTimeToLogWarning(ThreadDeadlockInfo threadDeadlockInfo, long currentTime) {
        if (currentTime - threadDeadlockInfo.getDetectedTime() <= DEADLOCK_WARNING_TIME) {
            return false;
        }

        return currentTime - threadDeadlockInfo.getLastLoggedTime() > DEADLOCK_LOG_INTERVAL;
    }

    private boolean hasSuppressionExpired(ThreadDeadlockInfo threadDeadlockInfo, long currentTime) {
        return currentTime - threadDeadlockInfo.getLastEventTime() > DEADLOCK_EVENT_SUPPRESSION_INTERVAL;
    }

    private void removeClearedDeadlocks(Set<Long> activeDeadlocks) {
        detectedDeadlockThreads.keySet().removeIf(threadId -> !activeDeadlocks.contains(threadId));
    }

    private ThreadDeadlockEvent convertToEvent(ThreadInfo threadInfo, long detectedTime) {
        return new ThreadDeadlockEvent(
            detectedTime,
            threadInfo.getThreadName(),
            threadInfo.getThreadId(),
            threadInfo.getThreadState().toString(),
            threadInfo.getBlockedCount(),
            threadInfo.getWaitedCount(),
            Optional.ofNullable(threadInfo.getLockName()).orElse("NONE"),
            threadInfo.getLockOwnerId() != -1 ? threadInfo.getLockOwnerId() : -1,
            Optional.ofNullable(threadInfo.getLockOwnerName()).orElse("NONE"),
            formatStackTrace(threadInfo.getStackTrace())
        );
    }

    private String formatStackTrace(StackTraceElement[] stackTrace) {
        return Arrays.stream(stackTrace)
            .map(StackTraceElement::toString)
            .collect(Collectors.joining("\n"));
    }

    static class ThreadDeadlockInfo {
        private long detectedTime;
        private long lastLoggedTime;
        private long lastEventTime;

        ThreadDeadlockInfo(long detectedTime) {
            this.detectedTime = detectedTime;
            this.lastLoggedTime = 0;
            this.lastEventTime = 0;
        }

        public long getDetectedTime() {
            return detectedTime;
        }

        public void setDetectedTime(long detectedTime) {
            this.detectedTime = detectedTime;
        }

        public long getLastLoggedTime() {
            return lastLoggedTime;
        }

        public void setLastLoggedTime(long lastLoggedTime) {
            this.lastLoggedTime = lastLoggedTime;
        }

        public long getLastEventTime() {
            return lastEventTime;
        }

        public void setLastEventTime(long lastEventTime) {
            this.lastEventTime = lastEventTime;
        }
    }
}
