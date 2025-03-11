package com.gnnny.deadlock4j.spring.boot.autoconfigure;

import com.gnnny.deadlock4j.bootstrap.Deadlock4jInitializer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class Deadlock4jLifecycle {

    private final Deadlock4jInitializer deadlock4jInitializer;

    public Deadlock4jLifecycle(Deadlock4jInitializer deadlock4jInitializer) {
        this.deadlock4jInitializer = deadlock4jInitializer;
    }

    @PostConstruct
    public void start() {
        deadlock4jInitializer.start();
    }

    @PreDestroy
    public void stop() {
        deadlock4jInitializer.stop();
    }
}
