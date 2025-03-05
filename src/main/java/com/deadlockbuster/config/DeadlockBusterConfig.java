package com.deadlockbuster.config;

import java.util.List;

public class DeadlockBusterConfig {
    public enum EventTransportType {
        TCP, QUEUE
    }

    private boolean enable;
    private boolean logEnable;
    private boolean sendEvent;
    private String centralServerIp;
    private int centralServerPort;
    private int monitoringInterval;
    private int heartbeatInterval;
    private EventTransportType eventTransportType;
    private List<String> databaseExceptionClasses;

    public DeadlockBusterConfig() {

    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isLogEnable() {
        return logEnable;
    }

    public void setLogEnable(boolean logEnable) {
        this.logEnable = logEnable;
    }

    public boolean isSendEvent() {
        return sendEvent;
    }

    public void setSendEvent(boolean sendEvent) {
        this.sendEvent = sendEvent;
    }

    public String getCentralServerIp() {
        return centralServerIp;
    }

    public void setCentralServerIp(String centralServerIp) {
        this.centralServerIp = centralServerIp;
    }

    public int getCentralServerPort() {
        return centralServerPort;
    }

    public void setCentralServerPort(int centralServerPort) {
        this.centralServerPort = centralServerPort;
    }

    public int getMonitoringInterval() {
        return monitoringInterval;
    }

    public void setMonitoringInterval(int monitoringInterval) {
        this.monitoringInterval = monitoringInterval;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public EventTransportType getEventTransportType() {
        return eventTransportType;
    }

    public void setEventTransportType(EventTransportType eventTransportType) {
        this.eventTransportType = eventTransportType;
    }

    public List<String> getDatabaseExceptionClasses() {
        return databaseExceptionClasses;
    }

    public void setDatabaseExceptionClasses(List<String> databaseExceptionClasses) {
        this.databaseExceptionClasses = databaseExceptionClasses;
    }

    @Override
    public String toString() {
        return "DeadlockBusterConfig{" +
            "enable=" + enable +
            ", logEnable=" + logEnable +
            ", sendEvent=" + sendEvent +
            ", centralServerIp='" + centralServerIp + '\'' +
            ", centralServerPort=" + centralServerPort +
            ", monitoringInterval=" + monitoringInterval +
            ", heartbeatInterval=" + heartbeatInterval +
            ", eventTransportType=" + eventTransportType +
            ", databaseExceptionClasses=" + databaseExceptionClasses +
            '}';
    }
}
