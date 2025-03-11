package com.gnnny.deadlock4j.transport.tcp;

import com.gnnny.deadlock4j.protobuf.MessageProto;
import com.gnnny.deadlock4j.transport.EventSender;

public interface TcpEventSender extends EventSender {
    void send(MessageProto message);
}
