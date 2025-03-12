package com.gnnny.deadlock4j.transport.queue;

import com.gnnny.deadlock4j.transport.DeadlockEventPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class QueueEventSenderTest {

    private QueueEventSender queueEventSender;

    @BeforeEach
    void set_up() {
        queueEventSender = mock(QueueEventSender.class);
    }

    @Test
    void should_send_event_to_queue() {
        // given
        DeadlockEventPayload event = mock(DeadlockEventPayload.class);

        // when
        queueEventSender.send(event);

        // then
        verify(queueEventSender).send(event);
    }
}
