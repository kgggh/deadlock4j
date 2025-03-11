package com.gnnny.deadlock4j.handler.database;

import com.gnnny.deadlock4j.detector.DeadlockDetector;
import com.gnnny.deadlock4j.event.DatabaseDeadlockEvent;
import com.gnnny.deadlock4j.exception.DatabaseDeadlockExceptionStore;
import com.gnnny.deadlock4j.handler.database.DatabaseDeadlockHandler;
import com.gnnny.deadlock4j.handler.database.DatabaseDeadlockHandlerManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DatabaseDeadlockHandlerManagerTest {

    private DeadlockDetector<DatabaseDeadlockEvent> detector;
    private DatabaseDeadlockHandlerManager manager;
    private DatabaseDeadlockHandler handler1;
    private DatabaseDeadlockHandler handler2;

    @BeforeEach
    void set_up() {
        detector = mock(DeadlockDetector.class);
        manager = new DatabaseDeadlockHandlerManager(detector);
        handler1 = mock(DatabaseDeadlockHandler.class);
        handler2 = mock(DatabaseDeadlockHandler.class);
        DatabaseDeadlockExceptionStore.clear();
    }

    @Test
    void should_register_handlers() {
        // given
        assertThat(manager).extracting("handlers").asList().isEmpty();

        // when
        manager.registerHandler(handler1);
        manager.registerHandler(handler2);

        // then
        assertThat(manager).extracting("handlers").asList()
            .hasSize(2)
            .contains(handler1, handler2);
    }

    @Test
    void should_execute_all_handlers_when_deadlock_detected() {
        // given
        DatabaseDeadlockEvent event = new DatabaseDeadlockEvent("SQLTransactionRollbackException", "40001", "Deadlock detected");
        List<DatabaseDeadlockEvent> events = List.of(event);

        when(detector.detect()).thenReturn(events);

        manager.registerHandler(handler1);
        manager.registerHandler(handler2);

        // when
        manager.executeHandlers();

        // then
        verify(handler1).handle(events);
        verify(handler2).handle(events);
        assertThat(DatabaseDeadlockExceptionStore.getAll()).isEmpty();
    }

    @Test
    void should_not_execute_handlers_when_no_deadlock_detected() {
        // given
        when(detector.detect()).thenReturn(List.of());

        manager.registerHandler(handler1);

        // when
        manager.executeHandlers();

        // then
        verifyNoInteractions(handler1);
        assertThat(DatabaseDeadlockExceptionStore.getAll()).isEmpty();
    }

    @Test
    void should_not_execute_handlers_when_no_handlers_registered() {
        // given
        DatabaseDeadlockEvent event = new DatabaseDeadlockEvent("SQLTransactionRollbackException", "40001", "Deadlock detected");
        List<DatabaseDeadlockEvent> events = List.of(event);

        when(detector.detect()).thenReturn(events);

        // when
        manager.executeHandlers();

        // then
        verifyNoInteractions(handler1);
        verifyNoInteractions(handler2);
        assertThat(DatabaseDeadlockExceptionStore.getAll()).isEmpty();
    }
}

