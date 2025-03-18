package com.example.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class NettyTcpConfig {

    @Value("${tcp.port}")
    private int port;

    @Value("${tcp.boss-threads}")
    private int bossThreads;

    @Value("${tcp.worker-threads}")
    private int workerThreads;

}

