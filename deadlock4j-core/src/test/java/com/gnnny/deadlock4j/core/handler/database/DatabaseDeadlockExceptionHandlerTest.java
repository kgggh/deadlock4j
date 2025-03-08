package com.gnnny.deadlock4j.core.handler.database;

import com.gnnny.deadlock4j.core.exception.DatabaseDeadlockExceptionChecker;
import com.gnnny.deadlock4j.core.exception.DatabaseDeadlockExceptionStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatabaseDeadlockExceptionHandlerTest {

    private DatabaseDeadlockExceptionChecker exceptionChecker;
    private DatabaseDeadlockExceptionHandler handler;

    @BeforeEach
    void setUp() {
        exceptionChecker = mock(DatabaseDeadlockExceptionChecker.class);
        handler = new DatabaseDeadlockExceptionHandler(exceptionChecker);
        DatabaseDeadlockExceptionStore.clear();
    }

    @Test
    void should_add_deadlock_exception_to_store_when_detected() {
        // given
        Throwable deadlockException = new SQLException("Deadlock detected", "40001");
        when(exceptionChecker.isDeadlockException(deadlockException)).thenReturn(true);

        // when
        handler.uncaughtException(Thread.currentThread(), deadlockException);

        // then
        assertThat(DatabaseDeadlockExceptionStore.peekAll()).isNotEmpty()
            .contains(deadlockException);
    }

    @Test
    void should_not_add_exception_to_store_when_not_deadlock() {
        // given
        Throwable non_deadlock_exception = new RuntimeException("Some other error");
        when(exceptionChecker.isDeadlockException(any(Throwable.class))).thenReturn(false);

        // when
        // then
        assertThatThrownBy(() -> handler.uncaughtException(Thread.currentThread(), non_deadlock_exception))
            .isInstanceOf(RuntimeException.class)
            .hasCause(non_deadlock_exception);

        assertThat(DatabaseDeadlockExceptionStore.peekAll()).isNotNull().isEmpty();
    }
}
