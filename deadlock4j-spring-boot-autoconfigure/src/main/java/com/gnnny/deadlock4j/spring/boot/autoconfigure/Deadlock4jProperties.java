package com.gnnny.deadlock4j.spring.boot.autoconfigure;

import com.gnnny.deadlock4j.config.Deadlock4jConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "deadlock4j")
public class Deadlock4jProperties {
    private boolean logEnabled = true;
    private String instanceId = "application";
    private String tcpServerIp;
    private int tcpServerPort = 0;
    private int monitorInterval = 1000;
    private int heartbeatInterval = 30000;
    private Deadlock4jConfig.TransportType transportType = Deadlock4jConfig.TransportType.NONE;
    private List<String> detectDatabaseExceptionClasses;

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

    public Deadlock4jConfig.TransportType getTransportType() {
        return transportType;
    }

    public void setTransportType(Deadlock4jConfig.TransportType transportType) {
        this.transportType = transportType;
    }

    public List<String> getDetectDatabaseExceptionClasses() {
        return detectDatabaseExceptionClasses;
    }

    public void setDetectDatabaseExceptionClasses(List<String> detectDatabaseExceptionClasses) {
        this.detectDatabaseExceptionClasses = detectDatabaseExceptionClasses;
    }
}
