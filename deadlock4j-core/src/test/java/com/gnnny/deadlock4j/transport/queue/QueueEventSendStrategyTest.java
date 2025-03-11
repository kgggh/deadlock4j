package com.gnnny.deadlock4j.transport.queue;

import com.gnnny.deadlock4j.event.DeadlockEvent;
import com.gnnny.deadlock4j.transport.queue.QueueEventSendStrategy;
import com.gnnny.deadlock4j.transport.queue.QueueEventSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class QueueEventSendStrategyTest {

    private QueueEventSender queueEventSender;
    private QueueEventSendStrategy sendStrategy;

    @BeforeEach
    void set_up() {
        queueEventSender = mock(QueueEventSender.class);
        sendStrategy = new QueueEventSendStrategy(queueEventSender);
    }

    @Test
    void should_send_event_to_queue() {
        // given
        DeadlockEvent event = mock(DeadlockEvent.class);

        // when
        sendStrategy.send(event);

        // then
        verify(queueEventSender).send(event);
    }
}
