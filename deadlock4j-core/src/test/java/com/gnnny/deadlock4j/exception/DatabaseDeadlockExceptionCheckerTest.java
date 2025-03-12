package com.gnnny.deadlock4j.exception;

import com.gnnny.deadlock4j.event.DatabaseDeadlockEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.SQLTransactionRollbackException;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseDeadlockExceptionCheckerTest {

    private DatabaseDeadlockExceptionChecker checker;

    @BeforeEach
    void set_up() {
        checker = new DatabaseDeadlockExceptionChecker();
    }

    @Test
    void is_deadlock_exception_returns_true_for_registered_exception() {
        // given
        Throwable exception = new SQLTransactionRollbackException("Deadlock detected", "40001");

        // when
        boolean result = checker.isDeadlockException(exception);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void is_deadlock_exception_returns_false_for_unregistered_exception() {
        // given
        Throwable exception = new RuntimeException("Not a deadlock");

        // when
        boolean result = checker.isDeadlockException(exception);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void create_deadlock_event_creates_event_correctly() {
        // given
        Throwable exception = new SQLException("Deadlock detected", "40001");

        // when
        DatabaseDeadlockEvent event = checker.createDeadlockEvent(System.currentTimeMillis(), exception);

        // then
        assertThat(event).isNotNull();
        assertThat(event.getExceptionName()).isEqualTo("SQLException");
        assertThat(event.getSqlState()).isEqualTo("40001");
        assertThat(event.getReason()).isEqualTo("Deadlock detected");
    }
}
