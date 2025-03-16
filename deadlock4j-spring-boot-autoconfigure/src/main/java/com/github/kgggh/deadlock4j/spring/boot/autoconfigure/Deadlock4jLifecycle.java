package com.github.kgggh.deadlock4j.spring.boot.autoconfigure;

import com.github.kgggh.deadlock4j.bootstrap.Deadlock4jInitializer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Deadlock4jLifecycle {
    private final Deadlock4jInitializer deadlock4jInitializer;

    @PostConstruct
    public void start() {
        deadlock4jInitializer.start();
    }

    @PreDestroy
    public void stop() {
        deadlock4jInitializer.stop();
    }
}
