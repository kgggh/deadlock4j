package com.gnnny.deadlock4j.transport.heartbeat;

import com.gnnny.deadlock4j.config.Deadlock4jConfig;
import com.gnnny.deadlock4j.protobuf.ProtoBufConverter;
import com.gnnny.deadlock4j.protobuf.SystemEventProto;
import com.gnnny.deadlock4j.transport.tcp.TcpEventSender;

public class TcpHeartbeatSender implements HeartbeatSender {
    private final TcpEventSender tcpEventSender;
    private final Deadlock4jConfig config;

    public TcpHeartbeatSender(TcpEventSender tcpEventSender, Deadlock4jConfig config) {
        this.tcpEventSender = tcpEventSender;
        this.config = config;
    }

    @Override
    public void send() {
        String instanceId = config.getInstanceId();
        SystemEventProto heartbeatMessage = ProtoBufConverter.createHeartbeatMessage(instanceId);
        tcpEventSender.send(heartbeatMessage);
    }
}
