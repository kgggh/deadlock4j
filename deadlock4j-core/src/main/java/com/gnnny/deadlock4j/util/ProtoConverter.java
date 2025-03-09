package com.gnnny.deadlock4j.util;

import com.deadlock4j.proto.*;
import com.gnnny.deadlock4j.core.event.DatabaseDeadlockEvent;
import com.gnnny.deadlock4j.core.event.DeadlockEvent;
import com.gnnny.deadlock4j.core.event.ThreadDeadlockEvent;
import com.google.protobuf.Timestamp;

import java.time.Instant;

public class ProtoConverter {

    private static Timestamp toProtobufTimestamp(Instant instant) {
        return Timestamp.newBuilder()
            .setSeconds(instant.getEpochSecond())
            .setNanos(instant.getNano())
            .build();
    }

    public static MessageProto createHeartbeatMessage() {
        return MessageProto.newBuilder()
            .setType(MessageType.HEARTBEAT)
            .setHeartbeat(
                HeartbeatMessageProto.newBuilder()
                    .setMessage("alive")
                    .build()
            ).build();
    }

    public static ThreadDeadlockEventProto toProto(ThreadDeadlockEvent event) {
        return ThreadDeadlockEventProto.newBuilder()
            .setTimestamp(toProtobufTimestamp(event.getTimestamp()))
            .setThreadName(event.getThreadName())
            .setThreadId(event.getThreadId())
            .setThreadState(event.getThreadState())
            .build();
    }

    public static DatabaseDeadlockEventProto toProto(DatabaseDeadlockEvent event) {
        return DatabaseDeadlockEventProto.newBuilder()
            .setTimestamp(toProtobufTimestamp(event.getTimestamp()))
            .setExceptionName(event.getExceptionName())
            .setSqlState(event.getSqlState())
            .setReason(event.getReason())
            .build();
    }

    public static MessageProto toProto(DeadlockEvent event) {
        MessageProto.Builder messageBuilder = MessageProto.newBuilder();

        if (event instanceof ThreadDeadlockEvent threadEvent) {
            messageBuilder
                .setType(MessageType.THREAD_DEADLOCK)
                .setThreadDeadlock(toProto(threadEvent));
        } else if (event instanceof DatabaseDeadlockEvent dbEvent) {
            messageBuilder
                .setType(MessageType.DATABASE_DEADLOCK)
                .setDatabaseDeadlock(toProto(dbEvent));
        } else {
            throw new IllegalArgumentException("Unsupported DeadlockEvent type: " + event.getClass().getSimpleName());
        }


        return messageBuilder.build();
    }
}
