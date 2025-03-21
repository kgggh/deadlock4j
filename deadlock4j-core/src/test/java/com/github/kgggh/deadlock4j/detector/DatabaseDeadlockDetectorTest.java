package com.github.kgggh.deadlock4j.detector;

import com.github.kgggh.deadlock4j.event.DatabaseDeadlockEvent;
import com.github.kgggh.deadlock4j.exception.DatabaseDeadlockExceptionChecker;
import com.github.kgggh.deadlock4j.exception.DatabaseDeadlockExceptionStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatabaseDeadlockDetectorTest {

    private DatabaseDeadlockExceptionChecker exceptionChecker;
    private DatabaseDeadlockDetector detector;

    @BeforeEach
    void setUp() {
        exceptionChecker = mock(DatabaseDeadlockExceptionChecker.class);
        detector = new DatabaseDeadlockDetector(exceptionChecker);
    }

    @Test
    void detect_returns_events_when_deadlock_exception_exists() {
        // given
        Throwable deadlockException = new SQLException("Deadlock detected", "40001");
        DatabaseDeadlockExceptionStore.add(deadlockException);
        when(exceptionChecker.isDeadlockException(deadlockException)).thenReturn(true);
        when(exceptionChecker.createDeadlockEvent(anyLong(), eq(deadlockException)))
            .thenReturn(new DatabaseDeadlockEvent(0L, "SQLException", "40001", "Deadlock detected"));

        // when
        List<DatabaseDeadlockEvent> events = detector.detect();

        // then
        assertThat(events)
            .hasSize(1)
            .first()
            .usingRecursiveComparison()
            .ignoringFields("timestamp")
            .isEqualTo(new DatabaseDeadlockEvent(0L, "SQLException", "40001", "Deadlock detected"));
    }


    @Test
    void detect_returns_empty_list_when_no_deadlock_exception() {
        // given
        DatabaseDeadlockExceptionStore.clear();

        // when
        List<DatabaseDeadlockEvent> events = detector.detect();

        // then
        assertThat(events).isEmpty();
    }
}
