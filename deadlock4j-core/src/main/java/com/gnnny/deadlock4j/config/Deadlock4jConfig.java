package com.gnnny.deadlock4j.config;

import java.util.List;

public class Deadlock4jConfig {
    public enum TransportType {
        TCP, QUEUE, NONE
    }

    private boolean logEnabled;
    private String instanceId;
    private String tcpServerIp;
    private int tcpServerPort;
    private int monitorInterval;
    private int heartbeatInterval;
    private TransportType transportType;
    private List<String> detectDatabaseExceptionClasses;

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

    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getTcpServerIp() {
        return tcpServerIp;
    }

    public void setTcpServerIp(String tcpServerIp) {
        this.tcpServerIp = tcpServerIp;
    }

    public int getTcpServerPort() {
        return tcpServerPort;
    }

    public void setTcpServerPort(int tcpServerPort) {
        this.tcpServerPort = tcpServerPort;
    }

    public int getMonitorInterval() {
        return monitorInterval;
    }

    public void setMonitorInterval(int monitorInterval) {
        this.monitorInterval = monitorInterval;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }

    public List<String> getDetectDatabaseExceptionClasses() {
        return detectDatabaseExceptionClasses;
    }

    public void setDetectDatabaseExceptionClasses(List<String> detectDatabaseExceptionClasses) {
        this.detectDatabaseExceptionClasses = detectDatabaseExceptionClasses;
    }

    @Override
    public String toString() {
        return "DeadlockBusterConfig{" +
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
