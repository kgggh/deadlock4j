package com.example.controller;

import com.example.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RequiredArgsConstructor
@RestController
public class DeadlockTestController {
    private final PostService postService;
    private final Random random = new Random();

    @PostMapping("/deadlock-test/thread")
    public void threadDeadlockTest() {
        postService.threadDeadlock();
    }

    @PostMapping("/deadlock-test/db")
    public void dbDeadlockTest() {
        long postId1 = random.nextBoolean() ? 1L : 2L;
        long postId2 = (postId1 == 1L) ? 2L : 1L;

        postService.updatePost(postId1, postId2);
    }
}
