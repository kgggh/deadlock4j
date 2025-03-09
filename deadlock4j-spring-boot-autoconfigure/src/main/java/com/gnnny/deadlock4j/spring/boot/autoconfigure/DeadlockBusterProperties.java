package com.gnnny.deadlock4j.spring.boot.autoconfigure;

import com.gnnny.deadlock4j.config.DeadlockBusterConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "deadlock-buster")
public class DeadlockBusterProperties {
    private boolean detectEnabled = true;
    private boolean logEnabled = true;
    private String tcpServerIp = "127.0.0.1";
    private int tcpServerPort = 8282;
    private int monitorInterval = 1000;
    private int heartbeatInterval = 30000;
    private DeadlockBusterConfig.TransportType transportType = DeadlockBusterConfig.TransportType.NONE;
    private List<String> detectDatabaseExceptionClasses;

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

    public DeadlockBusterConfig.TransportType getTransportType() {
        return transportType;
    }

    public void setTransportType(DeadlockBusterConfig.TransportType transportType) {
        this.transportType = transportType;
    }

    public List<String> getDetectDatabaseExceptionClasses() {
        return detectDatabaseExceptionClasses;
    }

    public void setDetectDatabaseExceptionClasses(List<String> detectDatabaseExceptionClasses) {
        this.detectDatabaseExceptionClasses = detectDatabaseExceptionClasses;
    }
}
