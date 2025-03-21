package com.github.kgggh.deadlock4j.handler.thread;

import com.github.kgggh.deadlock4j.detector.ThreadDeadlockDetector;
import com.github.kgggh.deadlock4j.event.ThreadDeadlockEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ThreadDeadlockHandlerManagerTest {

    private ThreadDeadlockHandlerManager manager;
    private ThreadDeadlockDetector detector;
    private ThreadDeadlockHandler handler1;
    private ThreadDeadlockHandler handler2;

    @BeforeEach
    void setUp() {
        detector = mock(ThreadDeadlockDetector.class);
        manager = new ThreadDeadlockHandlerManager(detector);
        handler1 = mock(ThreadDeadlockHandler.class);
        handler2 = mock(ThreadDeadlockHandler.class);
    }

    @Test
    void register_handler_should_add_handler_to_list() {
        // given
        // when
        manager.registerHandler(handler1);
        manager.registerHandler(handler2);

        // then
        assertThat(manager).extracting("handlers").asList()
            .hasSize(2)
            .contains(handler1, handler2);
    }

    @Test
    void execute_handlers_should_call_all_handlers_when_deadlock_detected() {
        // given
        ThreadDeadlockEvent event = new ThreadDeadlockEvent(
            System.currentTimeMillis(),
            "thread-1",
            1L,
            "BLOCKED",
            5,
            3,
            "java.util.concurrent.locks.ReentrantLock",
            102L,
            "thread-2",
            "stackTrace info..."
        );
        List<ThreadDeadlockEvent> events = List.of(event);

        when(detector.detect()).thenReturn(events);

        manager.registerHandler(handler1);
        manager.registerHandler(handler2);

        // when
        manager.processHandlers();

        // then
        verify(handler1, times(1)).handle(events);
        verify(handler2, times(1)).handle(events);
    }

    @Test
    void execute_handlers_should_do_nothing_when_no_deadlock_detected() {
        // given
        when(detector.detect()).thenReturn(List.of());

        manager.registerHandler(handler1);

        // when
        manager.processHandlers();

        // then
        verify(handler1, never()).handle(any());
    }

    @Test
    void execute_handlers_should_do_nothing_when_no_handlers_registered() {
        // given
        ThreadDeadlockEvent event = new ThreadDeadlockEvent(
            System.currentTimeMillis(),
            "thread-1",
            1L,
            "BLOCKED",
            5,
            3,
            "java.util.concurrent.locks.ReentrantLock",
            102L,
            "thread-2",
            "stackTrace info..."
        );
        List<ThreadDeadlockEvent> events = List.of(event);

        when(detector.detect()).thenReturn(events);

        // when
        manager.processHandlers();

        // then
        verifyNoInteractions(handler1);
        verifyNoInteractions(handler2);
    }
}
