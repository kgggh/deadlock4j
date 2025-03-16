package com.github.kgggh.deadlock4j.detector;

import com.github.kgggh.deadlock4j.event.ThreadDeadlockEvent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadDeadlockDetectorTest {

    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    @Test
    void testDeadlockDetection() throws InterruptedException {
        //given
        CountDownLatch latch = new CountDownLatch(1);
        ThreadDeadlockDetector detector = new ThreadDeadlockDetector();

        Thread thread1 = new Thread(() -> {
            synchronized (lock1) {
                try {
                    latch.countDown();

                    Thread.sleep(1000);
                    synchronized (lock2) {
                        System.out.println("Thread1 acquired lock2");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (lock2) {
                try {
                    latch.await();

                    Thread.sleep(1000);
                    synchronized (lock1) {
                        System.out.println("Thread2 acquired lock1");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        thread1.start();
        thread2.start();

        Thread.sleep(3000);

        //when
        List<ThreadDeadlockEvent> detectedEvents = detector.detect();

        //then
        assertThat(detectedEvents).hasSize(2);

        assertThat(detectedEvents)
            .extracting(ThreadDeadlockEvent::getThreadName)
            .contains(thread1.getName(), thread2.getName());

        assertThat(detectedEvents)
            .extracting(ThreadDeadlockEvent::getThreadState)
            .containsAnyOf("BLOCKED", "WAITING");
    }
}
