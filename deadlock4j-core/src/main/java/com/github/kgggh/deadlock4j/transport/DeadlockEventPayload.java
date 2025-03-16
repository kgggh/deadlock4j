package com.github.kgggh.deadlock4j.transport;

import com.github.kgggh.deadlock4j.event.DeadlockEvent;

public class DeadlockEventPayload {
    private final String instanceId;
    private final DeadlockEvent deadlockEvent;

    public DeadlockEventPayload(String instanceId, DeadlockEvent deadlockEvent) {
        this.instanceId = instanceId;
        this.deadlockEvent = deadlockEvent;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public DeadlockEvent getDeadlockEvent() {
        return deadlockEvent;
    }

    @Override
    public String toString() {
        return "DeadlockEventPayload{" +
            "instanceId='" + instanceId + '\'' +
            ", deadlockEvent=" + deadlockEvent +
            '}';
    }
}
