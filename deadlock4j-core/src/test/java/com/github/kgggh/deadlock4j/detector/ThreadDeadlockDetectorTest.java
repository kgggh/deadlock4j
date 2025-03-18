package com.github.kgggh.deadlock4j.detector;

import com.github.kgggh.deadlock4j.event.ThreadDeadlockEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadDeadlockDetectorTest {

    private final ThreadDeadlockDetector detector = new ThreadDeadlockDetector();
    private Thread thread1;
    private Thread thread2;
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    @BeforeEach
    void clearPrevDeadlocks() {
        detector.detect();
    }

    @AfterEach
    void resetDeadlockThread() throws InterruptedException {
        releaseDeadlocks();
        if (thread1 != null && thread1.isAlive()) thread1.join(200);
        if (thread2 != null && thread2.isAlive()) thread2.join(200);
    }

    @Test
    void test_deadlock_detection() throws InterruptedException {
        // given
        CountDownLatch latch = new CountDownLatch(1);

        thread1 = new Thread(() -> {
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

        thread2 = new Thread(() -> {
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
        Thread.sleep(2000);

        // when
        List<ThreadDeadlockEvent> detectedEvents = detector.detect();

        // then
        assertThat(detectedEvents).hasSize(2);
        assertThat(detectedEvents)
            .extracting(ThreadDeadlockEvent::getThreadName)
            .contains(thread1.getName(), thread2.getName());
        assertThat(detectedEvents)
            .extracting(ThreadDeadlockEvent::getThreadState)
            .containsAnyOf("BLOCKED", "WAITING");
    }

    @Test
    void test_deadlock_logging_and_event_suppression() throws InterruptedException {
        // given
        CountDownLatch latch = new CountDownLatch(1);

        thread1 = new Thread(() -> {
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

        thread2 = new Thread(() -> {
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
        Thread.sleep(2000);

        // when
        List<ThreadDeadlockEvent> detectedEvents1 = detector.detect();
        assertThat(detectedEvents1).isNotEmpty();

        List<ThreadDeadlockEvent> detectedEvents2 = detector.detect();
        assertThat(detectedEvents2).isEmpty();
    }

    private void releaseDeadlocks() {
        new Thread(() -> {
            synchronized (lock1) {
                synchronized (lock2) {
                    System.out.println("Deadlock released~~~~~~~~~~~~");
                }
            }
        }).start();
    }
}
