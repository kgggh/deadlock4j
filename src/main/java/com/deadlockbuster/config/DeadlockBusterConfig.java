package com.deadlockbuster.config;

import java.util.Properties;

public class DeadlockBusterConfig {
    private boolean enable;
    private boolean logEnable;
    private boolean sendEvent;
    private String centralServerIp;
    private int centralServerPort;

    public DeadlockBusterConfig() {
        this.enable = true;
        this.logEnable = true;
        this.sendEvent = false;
        this.centralServerIp = null;
        this.centralServerPort = 8282;
    }

    private void applyProperties(Properties properties) {
        this.enable = Boolean.parseBoolean(properties.getProperty("deadlock-buster.enable", String.valueOf(enable)));
        this.logEnable = Boolean.parseBoolean(properties.getProperty("deadlock-buster.log-enable", String.valueOf(logEnable)));
        this.sendEvent = Boolean.parseBoolean(properties.getProperty("deadlock-buster.send-event", String.valueOf(sendEvent)));
        this.centralServerIp = properties.getProperty("deadlock-buster.central-server-ip", centralServerIp);
        this.centralServerPort = Integer.parseInt(properties.getProperty("deadlock-buster.central-server-port", String.valueOf(centralServerPort)));
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
}
