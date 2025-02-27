package com.deadlockbuster.core.detector;

import com.deadlockbuster.core.event.DeadlockEvent;

import java.util.List;

public interface DeadlockDetector {
    List<DeadlockEvent> detect();
}
