package com.github.kgggh.deadlock4j.handler.database;

import com.github.kgggh.deadlock4j.exception.DatabaseDeadlockExceptionChecker;
import com.github.kgggh.deadlock4j.exception.DatabaseDeadlockExceptionStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        try {
            handler.uncaughtException(Thread.currentThread(), deadlockException);
        } catch (RuntimeException ignored) { }

        // then
        verify(exceptionChecker).isDeadlockException(deadlockException);
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

        assertThat(DatabaseDeadlockExceptionStore.getAll()).isNotNull().isEmpty();
    }
}
