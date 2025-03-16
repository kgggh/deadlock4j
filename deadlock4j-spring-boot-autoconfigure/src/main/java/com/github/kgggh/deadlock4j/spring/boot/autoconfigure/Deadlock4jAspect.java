package com.github.kgggh.deadlock4j.spring.boot.autoconfigure;

import com.github.kgggh.deadlock4j.exception.DatabaseDeadlockExceptionChecker;
import com.github.kgggh.deadlock4j.exception.DatabaseDeadlockExceptionStore;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

@RequiredArgsConstructor
@Order(100)
@Aspect
public class Deadlock4jAspect {
    private final DatabaseDeadlockExceptionChecker checker;

    @AfterThrowing(pointcut =
        "@annotation(com.github.kgggh.deadlock4j.spring.boot.autoconfigure.DetectDatabaseDeadlock) || " +
        "@within(com.github.kgggh.deadlock4j.spring.boot.autoconfigure.DetectDatabaseDeadlock)",
        throwing = "ex")
    public void detectDeadlock(Exception ex) throws Exception {
        if (checker.isDeadlockException(ex)) {
            DatabaseDeadlockExceptionStore.add(ex);
        }

        throw ex;
    }
}
