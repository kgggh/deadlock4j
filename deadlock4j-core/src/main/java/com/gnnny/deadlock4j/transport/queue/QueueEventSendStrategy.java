package com.gnnny.deadlock4j.transport.queue;

import com.gnnny.deadlock4j.event.DeadlockEvent;
import com.gnnny.deadlock4j.transport.EventSendStrategy;

public class QueueEventSendStrategy implements EventSendStrategy {
    private final QueueEventSender queueEventSender;

    public QueueEventSendStrategy(QueueEventSender queueEventSender) {
        this.queueEventSender = queueEventSender;
    }

    @Override
    public void send(DeadlockEvent event) {
        queueEventSender.send(event);
    }
}
