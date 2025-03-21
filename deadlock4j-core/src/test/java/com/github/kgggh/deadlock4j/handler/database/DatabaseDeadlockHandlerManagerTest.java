package com.github.kgggh.deadlock4j.handler.database;

import com.github.kgggh.deadlock4j.detector.DeadlockDetector;
import com.github.kgggh.deadlock4j.event.DatabaseDeadlockEvent;
import com.github.kgggh.deadlock4j.exception.DatabaseDeadlockExceptionStore;
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
    void setUp() {
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
        DatabaseDeadlockEvent event = new DatabaseDeadlockEvent(System.currentTimeMillis(), "SQLTransactionRollbackException", "40001", "Deadlock detected");
        List<DatabaseDeadlockEvent> events = List.of(event);

        when(detector.detect()).thenReturn(events);

        manager.registerHandler(handler1);
        manager.registerHandler(handler2);

        // when
        manager.processHandlers();

        // then
        assertThat(DatabaseDeadlockExceptionStore.getAll()).isEmpty();
    }

    @Test
    void should_not_execute_handlers_when_no_deadlock_detected() {
        // given
        when(detector.detect()).thenReturn(List.of());

        manager.registerHandler(handler1);

        // when
        manager.processHandlers();

        // then
        verifyNoInteractions(handler1);
        assertThat(DatabaseDeadlockExceptionStore.getAll()).isEmpty();
    }

    @Test
    void should_not_execute_handlers_when_no_handlers_registered() {
        // given
        DatabaseDeadlockEvent event = new DatabaseDeadlockEvent(System.currentTimeMillis(), "SQLTransactionRollbackException", "40001", "Deadlock detected");
        List<DatabaseDeadlockEvent> events = List.of(event);

        when(detector.detect()).thenReturn(events);

        // when
        manager.processHandlers();

        // then
        verifyNoInteractions(handler1);
        verifyNoInteractions(handler2);
        assertThat(DatabaseDeadlockExceptionStore.getAll()).isEmpty();
    }
}

