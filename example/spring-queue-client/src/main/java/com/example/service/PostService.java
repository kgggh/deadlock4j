package com.example.service;

import com.example.domain.Post;
import com.example.domain.PostRepository;
import com.github.kgggh.deadlock4j.spring.boot.autoconfigure.DetectDatabaseDeadlock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
@DetectDatabaseDeadlock
public class PostService {
    private final PostRepository postRepository;
    private final Lock lock1 = new ReentrantLock();
    private final Lock lock2 = new ReentrantLock();

    public void threadDeadlock() {
        log.info("Starting thread deadlock simulation...");

        Thread thread1 = new Thread(() -> {
            lock1.lock();
            try {
                log.info("Thread 1: Acquired lock1");
                Thread.sleep(100);
                lock2.lock();
                try {
                    log.info("Thread 1: Acquired lock2");
                } finally {
                    lock2.unlock();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock1.unlock();
            }
        });

        Thread thread2 = new Thread(() -> {
            lock2.lock();
            try {
                log.info("Thread 2: Acquired lock2");
                Thread.sleep(100);
                lock1.lock();
                try {
                    log.info("Thread 2: Acquired lock1");
                } finally {
                    lock1.unlock();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock2.unlock();
            }
        });

        thread1.start();
        thread2.start();
    }

    @Transactional
    public void updatePost(Long postId, Long anotherPostId) {
        Post postA = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("post not found"));
        postA.setViews(postA.getViews() + 1);
        log.info("Post updated: {}", postA);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Post postB = postRepository.findById(anotherPostId)
            .orElseThrow(() -> new RuntimeException("post not found"));
        postB.setViews(postB.getViews() + 1);

        log.info("Post B updated: {}", postB);
    }
}
