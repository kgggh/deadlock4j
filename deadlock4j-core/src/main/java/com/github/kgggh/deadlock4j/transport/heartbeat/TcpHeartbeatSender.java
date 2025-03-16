package com.github.kgggh.deadlock4j.transport.heartbeat;

import com.github.kgggh.deadlock4j.config.Deadlock4jConfig;
import com.github.kgggh.deadlock4j.protobuf.ProtoBufConverter;
import com.github.kgggh.deadlock4j.protobuf.SystemEventProto;
import com.github.kgggh.deadlock4j.transport.tcp.TcpEventSender;

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
