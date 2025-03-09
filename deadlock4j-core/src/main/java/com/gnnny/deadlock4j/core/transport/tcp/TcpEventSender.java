package com.gnnny.deadlock4j.core.transport.tcp;

import com.deadlock4j.proto.MessageProto;
import com.gnnny.deadlock4j.core.transport.EventSender;

public interface TcpEventSender extends EventSender {
    void send(MessageProto message);
}
