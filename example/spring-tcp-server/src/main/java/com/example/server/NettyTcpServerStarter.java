package com.example.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NettyTcpServerStarter {

    private final NettyTcpServerManager nettyTcpServerManager;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        nettyTcpServerManager.start();
    }
}
