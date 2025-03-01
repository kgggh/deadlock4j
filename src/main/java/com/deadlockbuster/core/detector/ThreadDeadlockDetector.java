package com.deadlockbuster.core.detector;

import com.deadlockbuster.core.event.ThreadDeadlockEvent;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThreadDeadlockDetector implements DeadlockDetector<ThreadDeadlockEvent> {
    private final ThreadMXBean threadMXBean;

    public ThreadDeadlockDetector() {
        this.threadMXBean = ManagementFactory.getThreadMXBean();
    }

    @Override
    public List<ThreadDeadlockEvent> detect() {
        long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
        if (deadlockedThreads == null) {
            return Collections.emptyList();
        }

        List<ThreadDeadlockEvent> detectedEvents = new ArrayList<>();
        for (long threadId : deadlockedThreads) {
            ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId);

            if(threadInfo == null) {
                continue;
            }

            detectedEvents.add(new ThreadDeadlockEvent(
                threadInfo.getThreadName(),
                threadInfo.getThreadId(),
                threadInfo.getThreadState().name()
            ));
        }

        return detectedEvents;
    }
}
