package com.gnnny.deadlock4j.handler.database;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gnnny.deadlock4j.event.DatabaseDeadlockEvent;
import com.gnnny.deadlock4j.handler.database.DatabaseDeadlockLogHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseDeadlockLogHandlerTest {

    private DatabaseDeadlockLogHandler handler;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void set_up() {
        handler = new DatabaseDeadlockLogHandler();

        Logger logger = (Logger) LoggerFactory.getLogger(DatabaseDeadlockLogHandler.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void should_log_deadlock_event_when_detected() {
        // given
        DatabaseDeadlockEvent event = new DatabaseDeadlockEvent(
            "SQLTransactionRollbackException",
            "40001",
            "Deadlock detected"
        );

        List<DatabaseDeadlockEvent> events = List.of(event);

        // when
        handler.handle(events);

        // then
        assertThat(listAppender.list)
            .isNotEmpty()
            .anyMatch(log -> log.getFormattedMessage().contains("[DEADLOCK DETECTED]"));
    }

    @Test
    void should_not_log_when_no_events() {
        // given
        List<DatabaseDeadlockEvent> events = List.of();

        // when
        handler.handle(events);

        // then
        assertThat(listAppender.list).isEmpty();
    }

    @Test
    void should_not_log_when_events_are_null() {
        // given
        List<DatabaseDeadlockEvent> events = null;

        // when
        handler.handle(events);

        // then
        assertThat(listAppender.list).isEmpty();
    }
}
