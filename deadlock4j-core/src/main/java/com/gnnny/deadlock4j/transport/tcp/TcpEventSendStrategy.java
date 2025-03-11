package com.gnnny.deadlock4j.transport.tcp;

import com.gnnny.deadlock4j.event.DeadlockEvent;
import com.gnnny.deadlock4j.protobuf.MessageProto;
import com.gnnny.deadlock4j.protobuf.ProtoConverter;
import com.gnnny.deadlock4j.transport.EventSendStrategy;

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
