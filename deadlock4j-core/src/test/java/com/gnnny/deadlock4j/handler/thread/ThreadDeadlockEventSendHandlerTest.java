package com.gnnny.deadlock4j.handler.thread;

import com.gnnny.deadlock4j.config.Deadlock4jConfig;
import com.gnnny.deadlock4j.event.ThreadDeadlockEvent;
import com.gnnny.deadlock4j.transport.DeadlockEventPayload;
import com.gnnny.deadlock4j.transport.EventSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class ThreadDeadlockEventSendHandlerTest {

    private EventSender eventSender;
    private ThreadDeadlockEventSendHandler handler;

    @BeforeEach
    void setUp() {
        eventSender = mock(EventSender.class);
        Deadlock4jConfig config = mock(Deadlock4jConfig.class);
        handler = new ThreadDeadlockEventSendHandler(eventSender, config);
    }

    @Test
    void handle_should_send_all_deadlock_events() {
        // given
        DeadlockEventPayload eventPayload1 = new DeadlockEventPayload("instanceId",
            new ThreadDeadlockEvent(
                System.currentTimeMillis(),
                "thread-1",
                101L,
                "BLOCKED",
                5,
                3,
                "java.util.concurrent.locks.ReentrantLock",
                102L,
                "thread-2",
                "stackTrace info..."
            )
        );

        DeadlockEventPayload eventPayload2 = new DeadlockEventPayload("instanceId",
            new ThreadDeadlockEvent(
                System.currentTimeMillis(),
                "thread-2",
                102L,
                "WAITING",
                2,
                1,
                "java.util.concurrent.locks.ReentrantLock",
                101L,
                "thread-1",
                "stackTrace info..."
            )
        );

        List<ThreadDeadlockEvent> events = List.of(
            (ThreadDeadlockEvent) eventPayload1.getDeadlockEvent(),
            (ThreadDeadlockEvent) eventPayload2.getDeadlockEvent()
        );

        // when
        handler.handle(events);

        // then
        verify(eventSender, times(2)).send(any(DeadlockEventPayload.class));
    }


    @Test
    void handle_should_do_nothing_when_no_events() {
        // given
        List<ThreadDeadlockEvent> events = List.of();

        // when
        handler.handle(events);

        // then
        verifyNoInteractions(eventSender);
    }

    @Test
    void handle_should_do_nothing_when_events_are_null() {
        // given
        List<ThreadDeadlockEvent> events = null;

        // when
        handler.handle(events);

        // then
        verifyNoInteractions(eventSender);
    }
}

