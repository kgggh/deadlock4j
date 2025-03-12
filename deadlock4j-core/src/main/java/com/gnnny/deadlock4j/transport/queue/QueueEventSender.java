package com.gnnny.deadlock4j.transport.queue;

import com.gnnny.deadlock4j.transport.DeadlockEventPayload;
import com.gnnny.deadlock4j.transport.EventSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueEventSender implements EventSender {
    private static final Logger LOG = LoggerFactory.getLogger(QueueEventSender.class);

    static {
        LOG.warn("QueueEventSender is not implemented!");
    }

    @Override
    public void send(DeadlockEventPayload eventPayload) {
        LOG.warn("QueueEventSender is not implemented! Dropping event: {}", eventPayload);
    }
}
