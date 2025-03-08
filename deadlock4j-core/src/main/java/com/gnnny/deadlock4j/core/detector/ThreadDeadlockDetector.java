package com.gnnny.deadlock4j.core.detector;

import com.gnnny.deadlock4j.core.event.ThreadDeadlockEvent;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.*;

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

            ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId);
            if(threadInfo != null) {
                detectedEvents.add(new ThreadDeadlockEvent(
                    threadInfo.getThreadName(),
                    threadInfo.getThreadId(),
                    threadInfo.getThreadState().name()
                ));
            }
        }

        return detectedEvents;
    }
}
