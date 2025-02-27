package com.deadlockbuster.core.detector;

import com.deadlockbuster.core.event.DeadlockEvent;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

public class ThreadDeadlockDetector implements DeadlockDetector {

    private final ThreadMXBean threadMXBean;

    public ThreadDeadlockDetector() {
        this.threadMXBean = ManagementFactory.getThreadMXBean();
    }

    @Override
    public List<DeadlockEvent> detect() {
        long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
        List<DeadlockEvent> detectedEvents = new ArrayList<>();
        if (deadlockedThreads == null) {
            return detectedEvents;
        }

        for (long threadId : deadlockedThreads) {
            ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId);

            if(threadInfo == null) {
                continue;
            }

            DeadlockEvent event = new DeadlockEvent(
                DeadlockEvent.DeadlockType.THREAD,
                threadInfo.getThreadName(),
                threadInfo.getThreadId(),
                threadInfo.getThreadState().name()
            );

            detectedEvents.add(event);
        }
        return detectedEvents;
    }
}
