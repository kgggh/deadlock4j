package com.gnnny.deadlock4j.core.detector;


import com.gnnny.deadlock4j.core.event.DeadlockEvent;

import java.util.List;

public interface DeadlockDetector<T extends DeadlockEvent> {
    List<T> detect();
}
