package com.deadlockbuster.config;

import java.util.List;

public class DeadlockBusterConfig {
    public enum TransportType {
        TCP, QUEUE
    }

    private boolean detectEnabled;
    private boolean logEnabled;
    private boolean sendEnabled;
    private String tcpServerIp;
    private int tcpServerPort;
    private int monitorInterval;
    private int heartbeatInterval;
    private TransportType transportType;
    private List<String> detectDatabaseExceptionClasses;

    public DeadlockBusterConfig() {

    }

    public boolean isDetectEnabled() {
        return detectEnabled;
    }

    public void setDetectEnabled(boolean detectEnabled) {
        this.detectEnabled = detectEnabled;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }

    public boolean isSendEnabled() {
        return sendEnabled;
    }

    public void setSendEnabled(boolean sendEnabled) {
        this.sendEnabled = sendEnabled;
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

    public TransportType getEventTransportType() {
        return transportType;
    }

    public void setEventTransportType(TransportType transportType) {
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
            "enable=" + detectEnabled +
            ", logEnable=" + logEnabled +
            ", sendEvent=" + sendEnabled +
            ", centralServerIp='" + tcpServerIp + '\'' +
            ", centralServerPort=" + tcpServerPort +
            ", monitoringInterval=" + monitorInterval +
            ", heartbeatInterval=" + heartbeatInterval +
            ", eventTransportType=" + transportType +
            ", databaseExceptionClasses=" + detectDatabaseExceptionClasses +
            '}';
    }
}
