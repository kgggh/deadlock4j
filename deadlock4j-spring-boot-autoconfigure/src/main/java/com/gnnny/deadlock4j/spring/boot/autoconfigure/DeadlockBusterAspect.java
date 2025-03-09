package com.gnnny.deadlock4j.spring.boot.autoconfigure;

import com.gnnny.deadlock4j.core.exception.DatabaseDeadlockExceptionChecker;
import com.gnnny.deadlock4j.core.exception.DatabaseDeadlockExceptionStore;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class DeadlockBusterAspect {
    private final DatabaseDeadlockExceptionChecker deadlockExceptionChecker;

    public DeadlockBusterAspect(DatabaseDeadlockExceptionChecker deadlockExceptionChecker) {
        this.deadlockExceptionChecker = deadlockExceptionChecker;
    }

    @Pointcut("within(@org.springframework.stereotype.Repository *) || @annotation(org.springframework.transaction.annotation.Transactional)")
    private void databaseOperations() {}

    @Around("databaseOperations()")
    public Object detectDeadlock(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            if (deadlockExceptionChecker.isDeadlockException(e)) {
                DatabaseDeadlockExceptionStore.add(e);
            }
            throw e;
        }
    }
}
