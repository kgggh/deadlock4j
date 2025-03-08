package com.gnnny.deadlock4j.core.transport.tcp;

import com.deadlockbuster.proto.MessageProto;
import com.gnnny.deadlock4j.core.event.DeadlockEvent;
import com.gnnny.deadlock4j.core.transport.EventSendStrategy;
import com.gnnny.deadlock4j.util.ProtoConverter;

public class TcpEventSendStrategy implements EventSendStrategy {
    private final TcpEventSender tcpEventSender;

    public TcpEventSendStrategy(TcpEventSender tcpEventSender) {
        this.tcpEventSender = tcpEventSender;
    }

    @Override
    public void send(DeadlockEvent event) {
        MessageProto message = ProtoConverter.toProto(event);
        tcpEventSender.send(message);
    }
}
