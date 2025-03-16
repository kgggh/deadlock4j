package com.github.kgggh.deadlock4j.detector;

import com.github.kgggh.deadlock4j.event.ThreadDeadlockEvent;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.*;
import java.util.stream.Collectors;

public class ThreadDeadlockDetector implements DeadlockDetector<ThreadDeadlockEvent> {
    private final ThreadMXBean threadMXBean;
    private final Set<Long> detectedDeadlockThreads = new HashSet<>();

    public ThreadDeadlockDetector() {
        this.threadMXBean = ManagementFactory.getThreadMXBean();
    }

    @Override
    public List<ThreadDeadlockEvent> detect() {
        long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
        if (deadlockedThreads == null) {
            detectedDeadlockThreads.clear();

            return Collections.emptyList();
        }

        List<ThreadDeadlockEvent> detectedEvents = new ArrayList<>();
        for(long threadId : deadlockedThreads) {
            if (!detectedDeadlockThreads.add(threadId)) {
                continue;
            }

            ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId, Integer.MAX_VALUE);
            if (threadInfo != null) {
                detectedDeadlockThreads.add(threadId);
                ThreadDeadlockEvent event = convertToEvent(threadInfo);
                detectedEvents.add(event);
            }
        }

        return detectedEvents;
    }

    private ThreadDeadlockEvent convertToEvent(ThreadInfo threadInfo) {
        return new ThreadDeadlockEvent(
            System.currentTimeMillis(),
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
}
