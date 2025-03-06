package com.deadlockbuster.core.exception;

import com.deadlockbuster.core.event.DatabaseDeadlockEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDeadlockExceptionChecker {
    private final List<Class<? extends Throwable>> deadlockExceptions = new ArrayList<>();

    public DatabaseDeadlockExceptionChecker(List<String> exceptionClasses) {
        registerDefaultDeadlockExceptions();
        registerCustomDeadlockExceptions(exceptionClasses);
    }

    public DatabaseDeadlockExceptionChecker() {
        registerDefaultDeadlockExceptions();
    }

    private void registerDefaultDeadlockExceptions() {
        addDeadlockException("java.sql.SQLTransactionRollbackException");
        addDeadlockException("org.springframework.dao.CannotAcquireLockException");
        addDeadlockException("org.springframework.dao.DeadlockLoserDataAccessException");
    }

    private void registerCustomDeadlockExceptions(List<String> exceptionClasses) {
        if (exceptionClasses != null) {
            for (String className : exceptionClasses) {
                addDeadlockException(className);
            }
        }
    }

    public void addDeadlockException(Class<? extends Throwable> exceptionClass) {
        if (!deadlockExceptions.contains(exceptionClass)) {
            deadlockExceptions.add(exceptionClass);
        }
    }

    public void addDeadlockException(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            if (Throwable.class.isAssignableFrom(clazz)) {
                addDeadlockException((Class<? extends Throwable>) clazz);
            }
        } catch (ClassNotFoundException ignored) { }
    }

    public boolean isDeadlockException(Throwable e) {
        return deadlockExceptions.stream().anyMatch(ex -> ex.isAssignableFrom(e.getClass()));
    }

    public DatabaseDeadlockEvent createDeadlockEvent(Throwable e) {
        return new DatabaseDeadlockEvent(
            e.getClass().getSimpleName(),
            extractSqlState(e),
            e.getMessage()
        );
    }

    private String extractSqlState(Throwable e) {
        if (e instanceof SQLException sqlEx) {
            return sqlEx.getSQLState();
        }

        Throwable cause = e.getCause();
        if (cause instanceof SQLException sqlEx) {
            return sqlEx.getSQLState();
        }

        return "UNKNOWN";
    }
}
