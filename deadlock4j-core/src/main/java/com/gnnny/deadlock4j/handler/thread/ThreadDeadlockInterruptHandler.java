package com.gnnny.deadlock4j.handler.thread;

import com.gnnny.deadlock4j.event.ThreadDeadlockEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadDeadlockInterruptHandler implements ThreadDeadlockHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadDeadlockInterruptHandler.class.getName());

    @Override
    public void handle(List<ThreadDeadlockEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        Map<Long, Thread> threadMap = new ConcurrentHashMap<>();
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            threadMap.put(thread.getId(), thread);
        }

        for (ThreadDeadlockEvent event : events) {
            Thread deadlockedThread = threadMap.get(event.getThreadId());

            if (deadlockedThread != null) {
                LOG.warn("Deadlock detected! [{}], trying to interrupt...", deadlockedThread.getName());
                deadlockedThread.interrupt();
            }
        }
    }
}
