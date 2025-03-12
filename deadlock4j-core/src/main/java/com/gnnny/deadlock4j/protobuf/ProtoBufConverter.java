package com.gnnny.deadlock4j.protobuf;

import com.gnnny.deadlock4j.event.DatabaseDeadlockEvent;
import com.gnnny.deadlock4j.event.DeadlockEvent;
import com.gnnny.deadlock4j.event.ThreadDeadlockEvent;
import com.gnnny.deadlock4j.transport.DeadlockEventPayload;

public class ProtoBufConverter {
    public static SystemEventProto createHeartbeatMessage(String instanceId) {
        return SystemEventProto.newBuilder()
            .setType(EventType.HEARTBEAT)
            .setInstanceId(instanceId)
            .setHeartbeat(
                HeartbeatEventProto.newBuilder()
                    .setMessage("alive")
                    .build()
            ).build();
    }

    public static ThreadDeadlockEventProto toThreadDeadlockProto(ThreadDeadlockEvent event) {
        return ThreadDeadlockEventProto.newBuilder()
            .setTimestamp(event.getTimestamp())
            .setThreadName(event.getThreadName())
            .setThreadId(event.getThreadId())
            .setThreadState(event.getThreadState())
            .setBlockedCount(event.getBlockedCount())
            .setWaitedCount(event.getWaitedCount())
            .setLockName(event.getLockName())
            .setLockOwnerId(event.getLockOwnerId())
            .setLockOwnerName(event.getLockOwnerName())
            .setStackTrace(event.getStackTrace())
            .build();
    }

    public static DatabaseDeadlockEventProto toDatabaseDeadlockProto(DatabaseDeadlockEvent event) {
        return DatabaseDeadlockEventProto.newBuilder()
            .setTimestamp(event.getTimestamp())
            .setExceptionName(event.getExceptionName())
            .setSqlState(event.getSqlState())
            .setReason(event.getReason())
            .build();
    }

    public static SystemEventProto convertDeadlockEventToProto(DeadlockEventPayload payload) {
        SystemEventProto.Builder messageBuilder = SystemEventProto.newBuilder()
            .setInstanceId(payload.getInstanceId());

        DeadlockEvent deadlockEvent = payload.getDeadlockEvent();
        if (deadlockEvent instanceof ThreadDeadlockEvent threadEvent) {
            messageBuilder
                .setType(EventType.THREAD_DEADLOCK)
                .setThreadDeadlock(toThreadDeadlockProto(threadEvent));
        } else if (deadlockEvent instanceof DatabaseDeadlockEvent dbEvent) {
            messageBuilder
                .setType(EventType.DATABASE_DEADLOCK)
                .setDatabaseDeadlock(toDatabaseDeadlockProto(dbEvent));
        } else {
            throw new IllegalArgumentException("Unsupported DeadlockEvent type: " + deadlockEvent.getClass().getSimpleName());
        }

        return messageBuilder.build();
    }
}
