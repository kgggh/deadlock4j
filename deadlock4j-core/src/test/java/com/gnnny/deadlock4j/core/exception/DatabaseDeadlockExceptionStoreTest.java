package com.gnnny.deadlock4j.core.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.SQLTransactionRollbackException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseDeadlockExceptionStoreTest {

    @BeforeEach
    void set_up() {
        DatabaseDeadlockExceptionStore.clear();
    }

    @Test
    void add_stores_exceptions_in_queue() {
        // given
        Throwable exception1 = new SQLTransactionRollbackException("Deadlock detected");
        Throwable exception2 = new RuntimeException("Another error");

        // when
        DatabaseDeadlockExceptionStore.add(exception1);
        DatabaseDeadlockExceptionStore.add(exception2);

        // then
        List<Throwable> exceptions = DatabaseDeadlockExceptionStore.peekAll();
        assertThat(exceptions).hasSize(2);
        assertThat(exceptions).containsExactly(exception1, exception2);
    }

    @Test
    void add_does_not_exceed_max_queue_size() {
        // given
        for (int i = 0; i < 35; i++) {
            DatabaseDeadlockExceptionStore.add(new SQLException("Exception " + i));
        }

        // when
        List<Throwable> exceptions = DatabaseDeadlockExceptionStore.peekAll();

        // then
        assertThat(exceptions).hasSize(30);
    }

    @Test
    void peek_all_returns_all_exceptions() {
        // given
        Throwable exception1 = new SQLException("SQL Error");
        Throwable exception2 = new RuntimeException("Runtime Error");

        DatabaseDeadlockExceptionStore.add(exception1);
        DatabaseDeadlockExceptionStore.add(exception2);

        // when
        List<Throwable> exceptions = DatabaseDeadlockExceptionStore.peekAll();

        // then
        assertThat(exceptions).containsExactly(exception1, exception2);
    }

    @Test
    void clear_removes_all_exceptions() {
        // given
        DatabaseDeadlockExceptionStore.add(new SQLException("Deadlock detected"));

        // when
        DatabaseDeadlockExceptionStore.clear();
        List<Throwable> exceptions = DatabaseDeadlockExceptionStore.peekAll();

        // then
        assertThat(exceptions).isEmpty();
    }
}

