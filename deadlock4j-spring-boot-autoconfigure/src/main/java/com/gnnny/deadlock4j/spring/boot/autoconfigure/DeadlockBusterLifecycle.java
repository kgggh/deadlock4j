package com.gnnny.deadlock4j.spring.boot.autoconfigure;

import com.gnnny.deadlock4j.Deadlock4jInitializer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class DeadlockBusterLifecycle {

    private final Deadlock4jInitializer deadlock4jInitializer;

    public DeadlockBusterLifecycle(Deadlock4jInitializer deadlock4jInitializer) {
        this.deadlock4jInitializer = deadlock4jInitializer;
    }

    @PostConstruct
    public void start() {
        log.info("Starting DeadlockBuster...");
        deadlock4jInitializer.start();
    }

    @PreDestroy
    public void stop() {
        log.info("Stopping DeadlockBuster...");
        deadlock4jInitializer.stop();
    }
}
