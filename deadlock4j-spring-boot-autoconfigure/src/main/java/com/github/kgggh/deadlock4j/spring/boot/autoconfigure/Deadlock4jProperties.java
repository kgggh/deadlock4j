package com.github.kgggh.deadlock4j.spring.boot.autoconfigure;

import com.github.kgggh.deadlock4j.config.Deadlock4jConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "deadlock4j")
public class Deadlock4jProperties {
    private boolean enabled = false;
    private boolean logEnabled = true;
    private String instanceId = "application";
    private String tcpServerIp;
    private int tcpServerPort = 0;
    private int monitorInterval = 1000;
    private int heartbeatInterval = 30000;
    private Deadlock4jConfig.TransportType transportType = Deadlock4jConfig.TransportType.NONE;
}
