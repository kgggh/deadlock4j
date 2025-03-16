package com.github.kgggh.deadlock4j.detector;


import com.github.kgggh.deadlock4j.event.DeadlockEvent;

import java.util.List;

public interface DeadlockDetector<T extends DeadlockEvent> {
    List<T> detect();
}
