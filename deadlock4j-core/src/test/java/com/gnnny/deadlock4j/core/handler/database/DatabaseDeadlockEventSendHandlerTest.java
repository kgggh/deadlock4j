package com.gnnny.deadlock4j.core.handler.database;

import com.gnnny.deadlock4j.core.event.DatabaseDeadlockEvent;
import com.gnnny.deadlock4j.core.event.DeadlockEvent;
import com.gnnny.deadlock4j.core.transport.EventSendStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class DatabaseDeadlockEventSendHandlerTest {

    private EventSendStrategy sendStrategy;
    private DatabaseDeadlockEventSendHandler handler;

    @BeforeEach
    void setUp() {
        sendStrategy = mock(EventSendStrategy.class);
        handler = new DatabaseDeadlockEventSendHandler(sendStrategy);
    }

    @Test
    void should_send_all_deadlock_events() {
        // given
        DeadlockEvent event1 = new DatabaseDeadlockEvent("SQLTransactionRollbackException", "40001", "Deadlock detected");
        DeadlockEvent event2 = new DatabaseDeadlockEvent("DeadlockLoserDataAccessException", "40002", "Another deadlock");

        List<DatabaseDeadlockEvent> events = List.of((DatabaseDeadlockEvent) event1, (DatabaseDeadlockEvent) event2);

        // when
        handler.handle(events);

        // then
        verify(sendStrategy).send(event1);
        verify(sendStrategy).send(event2);
    }

    @Test
    void should_not_send_when_no_events() {
        // given
        List<DatabaseDeadlockEvent> events = List.of();

        // when
        handler.handle(events);

        // then
        verifyNoInteractions(sendStrategy);
    }

    @Test
    void should_not_send_when_events_are_null() {
        // given
        List<DatabaseDeadlockEvent> events = null;

        // when
        handler.handle(events);

        // then
        verifyNoInteractions(sendStrategy);
    }
}
