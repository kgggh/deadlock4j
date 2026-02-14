package com.github.kgggh.deadlock4j.handler.thread;

import com.github.kgggh.deadlock4j.detector.DeadlockDetector;
import com.github.kgggh.deadlock4j.event.ThreadDeadlockEvent;
import com.github.kgggh.deadlock4j.handler.AbstractDeadlockHandlerManager;

public class ThreadDeadlockHandlerManager extends AbstractDeadlockHandlerManager<ThreadDeadlockHandler, ThreadDeadlockEvent> {

    public ThreadDeadlockHandlerManager(DeadlockDetector<ThreadDeadlockEvent> detector) {
        super(detector);
    }
}
