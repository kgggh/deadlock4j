package com.gnnny.deadlock4j.transport;

import org.slf4j.Logger;

public class NoOpEventSender implements EventSender {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(NoOpEventSender.class);

    @Override
    public void send(DeadlockEventPayload eventPayload) {
        LOG.debug("TransportType is NONE. Ignoring event: {}", eventPayload);
    }
}
