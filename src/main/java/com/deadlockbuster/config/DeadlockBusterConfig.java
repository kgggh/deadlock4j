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
    private String queueName;

    public DeadlockBusterConfig() {
        this.enable = true;
        this.logEnable = true;
    }

    public DeadlockBusterConfig(boolean enable, boolean logEnable, boolean sendEvent, String centralServerIp, int centralServerPort, int monitoringInterval, int heartbeatInterval, EventTransportType eventTransportType, List<String> databaseExceptionClasses, String queueName) {
        this.enable = enable;
        this.logEnable = logEnable;
        this.sendEvent = sendEvent;
        this.centralServerIp = centralServerIp;
        this.centralServerPort = centralServerPort;
        this.monitoringInterval = monitoringInterval;
        this.heartbeatInterval = heartbeatInterval;
        this.eventTransportType = eventTransportType;
        this.databaseExceptionClasses = databaseExceptionClasses;
        this.queueName = queueName;
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

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}
