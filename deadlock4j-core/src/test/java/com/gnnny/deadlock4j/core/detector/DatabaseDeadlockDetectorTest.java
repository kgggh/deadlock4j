package com.gnnny.deadlock4j.core.detector;

import com.gnnny.deadlock4j.core.event.DatabaseDeadlockEvent;
import com.gnnny.deadlock4j.core.exception.DatabaseDeadlockExceptionChecker;
import com.gnnny.deadlock4j.core.exception.DatabaseDeadlockExceptionStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatabaseDeadlockDetectorTest {

    private DatabaseDeadlockExceptionChecker exceptionChecker;
    private DatabaseDeadlockDetector detector;

    @BeforeEach
    void set_up() {
        exceptionChecker = mock(DatabaseDeadlockExceptionChecker.class);
        detector = new DatabaseDeadlockDetector(exceptionChecker);
    }

    @Test
    void detect_returns_events_when_deadlock_exception_exists() {
        // given
        Throwable deadlockException = new SQLException("Deadlock detected", "40001");
        DatabaseDeadlockEvent expectedEvent = new DatabaseDeadlockEvent("SQLException", "40001", "Deadlock detected");

        DatabaseDeadlockExceptionStore.add(deadlockException);
        when(exceptionChecker.isDeadlockException(deadlockException)).thenReturn(true);
        when(exceptionChecker.createDeadlockEvent(deadlockException)).thenReturn(expectedEvent);

        // when
        List<DatabaseDeadlockEvent> events = detector.detect();

        // then
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isEqualTo(expectedEvent);
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
