package com.gnnny.deadlock4j.handler.database;

import com.gnnny.deadlock4j.config.Deadlock4jConfig;
import com.gnnny.deadlock4j.event.DatabaseDeadlockEvent;
import com.gnnny.deadlock4j.transport.DeadlockEventPayload;
import com.gnnny.deadlock4j.transport.EventSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class DatabaseDeadlockEventSendHandlerTest {

    private EventSender eventSender;
    private Deadlock4jConfig config;
    private DatabaseDeadlockEventSendHandler handler;

    @BeforeEach
    void setUp() {
        eventSender = mock(EventSender.class);
        config = mock(Deadlock4jConfig.class);
        when(config.getInstanceId()).thenReturn("instanceId");
        handler = new DatabaseDeadlockEventSendHandler(eventSender, config);
    }

    @Test
    void should_send_all_deadlock_events() {
        // given
        DatabaseDeadlockEvent event1 = new DatabaseDeadlockEvent(System.currentTimeMillis(), "SQLTransactionRollbackException", "40001", "Deadlock detected");
        DatabaseDeadlockEvent event2 = new DatabaseDeadlockEvent(System.currentTimeMillis(), "DeadlockLoserDataAccessException", "40002", "Another deadlock");

        List<DatabaseDeadlockEvent> events = List.of(event1, event2);

        // when
        handler.handle(events);

        // then
        verify(eventSender, times(2)).send(any(DeadlockEventPayload.class));
    }

    @Test
    void should_not_send_when_no_events() {
        // given
        List<DatabaseDeadlockEvent> events = List.of();

        // when
        handler.handle(events);

        // then
        verifyNoInteractions(eventSender);
    }

    @Test
    void should_not_send_when_events_are_null() {
        // given
        // when
        handler.handle(null);

        // then
        verifyNoInteractions(eventSender);
    }
}
