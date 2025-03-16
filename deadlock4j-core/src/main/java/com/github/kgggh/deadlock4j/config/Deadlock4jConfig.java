package com.github.kgggh.deadlock4j.config;

import java.util.Objects;

public class Deadlock4jConfig {
    public enum TransportType {
        TCP, QUEUE, NONE
    }

    private static final int MIN_MONITOR_INTERVAL = 500;
    private static final int MIN_HEARTBEAT_INTERVAL = 500;

    private final boolean enabled;
    private final boolean logEnabled;
    private final String instanceId;
    private final String tcpServerIp;
    private final int tcpServerPort;
    private final int monitorInterval;
    private final int heartbeatInterval;
    private final TransportType transportType;

    public Deadlock4jConfig(boolean enabled, boolean logEnabled, String instanceId, String tcpServerIp, int tcpServerPort, int monitorInterval, int heartbeatInterval, TransportType transportType) {
        this.enabled = enabled;
        this.logEnabled = logEnabled;
        this.instanceId = instanceId;
        this.tcpServerIp = tcpServerIp;
        this.tcpServerPort = tcpServerPort;
        this.monitorInterval = monitorInterval;
        this.heartbeatInterval = heartbeatInterval;
        this.transportType = transportType;

        validate();
    }

    private void validate() {
        if(!enabled) {
            return;
        }

        Objects.requireNonNull(instanceId, "Instance ID must not be null.");
        if (monitorInterval < MIN_MONITOR_INTERVAL) {
            throw new IllegalArgumentException("Monitor interval must be at least " + MIN_MONITOR_INTERVAL + " ms.");
        }

        Objects.requireNonNull(transportType, "TransportType must not be null.");
        if(transportType == TransportType.TCP) {
            Objects.requireNonNull(tcpServerIp, "TcpServerIp must not be null.");
        } else {
            return;
        }

        if (heartbeatInterval < MIN_HEARTBEAT_INTERVAL) {
            throw new IllegalArgumentException("Heartbeat interval must be at least " + MIN_HEARTBEAT_INTERVAL + " ms.");
        }
    }

    public boolean isEnabled() {
        return enabled;
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

    @Override
    public String toString() {
        return "Deadlock4jConfig{" +
            "enabled=" + enabled +
            ", logEnabled=" + logEnabled +
            ", instanceId='" + instanceId + '\'' +
            ", tcpServerIp='" + tcpServerIp + '\'' +
            ", tcpServerPort=" + tcpServerPort +
            ", monitorInterval=" + monitorInterval +
            ", heartbeatInterval=" + heartbeatInterval +
            ", transportType=" + transportType +
            '}';
    }
}
