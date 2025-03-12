package com.gnnny.deadlock4j.spring.boot.autoconfigure;

import com.gnnny.deadlock4j.exception.DatabaseDeadlockExceptionChecker;
import com.gnnny.deadlock4j.exception.DatabaseDeadlockExceptionStore;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.DynamicMethodMatcherPointcut;

import java.lang.reflect.Method;

@RequiredArgsConstructor
public class DeadlockMethodInterceptor extends DynamicMethodMatcherPointcut implements MethodInterceptor {
    private final DatabaseDeadlockExceptionChecker checker;
    private final String basePackage;

    @Override
    public boolean matches(Method method, Class<?> targetClass, Object... args) {
        return targetClass.getPackageName().startsWith(basePackage);
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return targetClass.getPackageName().startsWith(basePackage);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } catch (Throwable e) {
            if (checker.isDeadlockException(e)) {
                DatabaseDeadlockExceptionStore.add(e);
            }
            throw e;
        }
    }
}
