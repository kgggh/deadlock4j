package com.github.kgggh.deadlock4j.transport.heartbeat;

import com.github.kgggh.deadlock4j.config.Deadlock4jConfig;
import com.github.kgggh.deadlock4j.protobuf.ProtoBufConverter;
import com.github.kgggh.deadlock4j.protobuf.SystemEventProto;
import com.github.kgggh.deadlock4j.transport.tcp.TcpEventSender;

public class TcpHeartbeatSender implements HeartbeatSender {
    private final TcpEventSender tcpEventSender;
    private final Deadlock4jConfig deadlock4jConfig;

    public TcpHeartbeatSender(TcpEventSender tcpEventSender, Deadlock4jConfig deadlock4jConfig) {
        this.tcpEventSender = tcpEventSender;
        this.deadlock4jConfig = deadlock4jConfig;
    }

    @Override
    public void send() {
        String instanceId = deadlock4jConfig.getInstanceId();
        SystemEventProto heartbeatMessage = ProtoBufConverter.createHeartbeatMessage(instanceId);
        tcpEventSender.send(heartbeatMessage);
    }
}
