package com.gnnny.deadlock4j.handler.thread;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gnnny.deadlock4j.event.ThreadDeadlockEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadDeadlockInterruptHandlerTest {

    private ThreadDeadlockInterruptHandler handler;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void set_up() {
        handler = new ThreadDeadlockInterruptHandler();

        Logger logger = (Logger) LoggerFactory.getLogger(ThreadDeadlockInterruptHandler.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void handle_should_interrupt_deadlocked_threads() {
        // given
        CountDownLatch latch = new CountDownLatch(1);
        Thread testThread = new Thread(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        testThread.start();
        long threadId = testThread.getId();

        List<ThreadDeadlockEvent> events = List.of(new ThreadDeadlockEvent(
            System.currentTimeMillis(),
            "thread-1",
            threadId,
            "BLOCKED",
            5,
            3,
            "java.util.concurrent.locks.ReentrantLock",
            102L,
            "thread-2",
            "stackTrace info..."
            ));

        // when
        handler.handle(events);

        // then
        assertThat(testThread.isInterrupted()).isTrue();

        assertThat(listAppender.list)
            .isNotEmpty()
            .anyMatch(event -> event.getFormattedMessage().contains("Deadlock detected!"));
    }

    @Test
    void handle_should_do_nothing_when_no_events() {
        // given
        List<ThreadDeadlockEvent> events = List.of();

        // when
        handler.handle(events);

        // then
        assertThat(listAppender.list).isEmpty();
    }

    @Test
    void handle_should_do_nothing_when_events_are_null() {
        // given
        List<ThreadDeadlockEvent> events = null;

        // when
        handler.handle(events);

        // then
        assertThat(listAppender.list).isEmpty();
    }
}

