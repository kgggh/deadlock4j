package com.gnnny.deadlock4j.handler.thread;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gnnny.deadlock4j.event.ThreadDeadlockEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadDeadlockLogHandlerTest {

    private ThreadDeadlockLogHandler handler;
    private TestLogger testLogger;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void set_up() {
        handler = new ThreadDeadlockLogHandler();

        testLogger = TestLoggerFactory.getTestLogger(ThreadDeadlockLogHandler.class);
        testLogger.clear();

        Logger logger = (Logger) LoggerFactory.getLogger(ThreadDeadlockLogHandler.class);

        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void handle_logs_warning_when_deadlock_events_exist() {
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

        // when
        handler.handle(events);

        // then
        List<ILoggingEvent> logs = listAppender.list;
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getLevel().toString()).isEqualTo(Level.WARN.toString());
        assertThat(logs.get(0).getFormattedMessage()).contains("[DEADLOCK DETECTED]");
    }

    @Test
    void handle_does_nothing_when_event_list_is_empty() {
        // given
        List<ThreadDeadlockEvent> events = List.of();

        // when
        handler.handle(events);

        // then
        assertThat(testLogger.getLoggingEvents()).isEmpty();
    }
}
