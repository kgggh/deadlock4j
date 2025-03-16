package com.github.kgggh.deadlock4j.config;

import java.util.List;

public class Deadlock4jConfig {
    public enum TransportType {
        TCP, QUEUE, NONE
    }

    private final boolean logEnabled;
    private final String instanceId;
    private final String tcpServerIp;
    private final int tcpServerPort;
    private final int monitorInterval;
    private final int heartbeatInterval;
    private final TransportType transportType;
    private final List<String> detectDatabaseExceptionClasses;

    public Deadlock4jConfig(boolean logEnabled, String instanceId, String tcpServerIp, int tcpServerPort, int monitorInterval, int heartbeatInterval, TransportType transportType, List<String> detectDatabaseExceptionClasses) {
        this.logEnabled = logEnabled;
        this.instanceId = instanceId;
        this.tcpServerIp = tcpServerIp;
        this.tcpServerPort = tcpServerPort;
        this.monitorInterval = monitorInterval;
        this.heartbeatInterval = heartbeatInterval;
        this.transportType = transportType;
        this.detectDatabaseExceptionClasses = detectDatabaseExceptionClasses;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getTcpServerIp() {
        return tcpServerIp;
    }

    public int getTcpServerPort() {
        return tcpServerPort;
    }

    public int getMonitorInterval() {
        return monitorInterval;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public List<String> getDetectDatabaseExceptionClasses() {
        return detectDatabaseExceptionClasses;
    }

    @Override
    public String toString() {
        return "Deadlock4jConfig{" +
            "logEnabled=" + logEnabled +
            ", instanceId='" + instanceId + '\'' +
            ", tcpServerIp='" + tcpServerIp + '\'' +
            ", tcpServerPort=" + tcpServerPort +
            ", monitorInterval=" + monitorInterval +
            ", heartbeatInterval=" + heartbeatInterval +
            ", transportType=" + transportType +
            ", detectDatabaseExceptionClasses=" + detectDatabaseExceptionClasses +
            '}';
    }
}
