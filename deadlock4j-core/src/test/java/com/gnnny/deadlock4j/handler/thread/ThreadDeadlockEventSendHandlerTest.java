package com.gnnny.deadlock4j.handler.thread;

import com.gnnny.deadlock4j.event.ThreadDeadlockEvent;
import com.gnnny.deadlock4j.handler.thread.ThreadDeadlockEventSendHandler;
import com.gnnny.deadlock4j.transport.EventSendStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class ThreadDeadlockEventSendHandlerTest {

    private EventSendStrategy sendStrategy;
    private ThreadDeadlockEventSendHandler handler;

    @BeforeEach
    void set_up() {
        sendStrategy = mock(EventSendStrategy.class);
        handler = new ThreadDeadlockEventSendHandler(sendStrategy);
    }

    @Test
    void handle_should_send_all_deadlock_events() {
        // given
        ThreadDeadlockEvent event1 = new ThreadDeadlockEvent("thread-1", 101L, "BLOCKED");
        ThreadDeadlockEvent event2 = new ThreadDeadlockEvent("thread-2", 102L, "WAITING");
        List<ThreadDeadlockEvent> events = List.of(event1, event2);

        // when
        handler.handle(events);

        // then
        verify(sendStrategy, times(1)).send(event1);
        verify(sendStrategy, times(1)).send(event2);
    }

    @Test
    void handle_should_do_nothing_when_no_events() {
        // given
        List<ThreadDeadlockEvent> events = List.of();

        // when
        handler.handle(events);

        // then
        verifyNoInteractions(sendStrategy);
    }

    @Test
    void handle_should_do_nothing_when_events_are_null() {
        // given
        List<ThreadDeadlockEvent> events = null;

        // when
        handler.handle(events);

        // then
        verifyNoInteractions(sendStrategy);
    }
}

