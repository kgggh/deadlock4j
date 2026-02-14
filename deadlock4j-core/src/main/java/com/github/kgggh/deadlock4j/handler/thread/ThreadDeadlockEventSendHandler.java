package com.github.kgggh.deadlock4j.handler.thread;

import com.github.kgggh.deadlock4j.config.Deadlock4jConfig;
import com.github.kgggh.deadlock4j.handler.AbstractEventSendHandler;
import com.github.kgggh.deadlock4j.event.ThreadDeadlockEvent;
import com.github.kgggh.deadlock4j.transport.EventSender;

public class ThreadDeadlockEventSendHandler extends AbstractEventSendHandler<ThreadDeadlockEvent>
    implements ThreadDeadlockHandler {

    public ThreadDeadlockEventSendHandler(EventSender sender, Deadlock4jConfig deadlock4jConfig) {
        super(sender, deadlock4jConfig);
    }
}
