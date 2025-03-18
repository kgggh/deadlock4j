package com.example.config;

import com.github.kgggh.deadlock4j.transport.DeadlockEventPayload;
import com.github.kgggh.deadlock4j.transport.EventSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaDeadlockEventSender implements EventSender {
    private final KafkaTemplate<String, DeadlockEventPayload> kafkaTemplate;
    private static final String DEADLOCK_EVENT_TOPIC = "deadlock-event";
    private final AtomicInteger count = new AtomicInteger(0);

    @Override
    public void send(DeadlockEventPayload eventPayload) {
        log.info("Sending event to Kafka: total count={}, payload={}", count.incrementAndGet(), eventPayload);
        kafkaTemplate.send(DEADLOCK_EVENT_TOPIC, eventPayload);
    }
}
