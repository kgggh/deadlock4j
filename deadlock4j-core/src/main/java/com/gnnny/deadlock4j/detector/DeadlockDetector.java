package com.gnnny.deadlock4j.detector;


import com.gnnny.deadlock4j.event.DeadlockEvent;

import java.util.List;

public interface DeadlockDetector<T extends DeadlockEvent> {
    List<T> detect();
}
