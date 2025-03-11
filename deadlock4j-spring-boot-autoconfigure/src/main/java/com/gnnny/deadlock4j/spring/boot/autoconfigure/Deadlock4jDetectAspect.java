package com.gnnny.deadlock4j.spring.boot.autoconfigure;

import com.gnnny.deadlock4j.exception.DatabaseDeadlockExceptionChecker;
import com.gnnny.deadlock4j.exception.DatabaseDeadlockExceptionStore;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@RequiredArgsConstructor
public class Deadlock4jDetectAspect {
    private final DatabaseDeadlockExceptionChecker checker;

    @Around("@within(com.gnnny.deadlock4j.spring.boot.autoconfigure.DatabaseDeadlockSensitive)")
    public Object detectDeadlock(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            if(checker.isDeadlockException(e)) {
                DatabaseDeadlockExceptionStore.add(e);
            }

            throw e;
        }
    }
}
