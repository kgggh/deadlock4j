package com.github.kgggh.deadlock4j.exception;

import com.github.kgggh.deadlock4j.event.DatabaseDeadlockEvent;

import java.sql.SQLException;
import java.sql.SQLTransactionRollbackException;
import java.util.*;

public class DatabaseDeadlockExceptionChecker {
    private final Set<Class<? extends Throwable>> deadlockExceptions = new HashSet<>();

    enum DeadlockSQLState {
        MYSQL("40001"),
        POSTGRESQL("40P01"),
        ORACLE("ORA-00060"),
        MARIADB("40001"),
        H2("40001");

        private final String sqlState;
        private static final Map<String, List<String>> SQL_STATE_LOOKUP_MAP = new HashMap<>();

        static {
            for (DeadlockSQLState deadlock : values()) {
                SQL_STATE_LOOKUP_MAP
                    .computeIfAbsent(deadlock.getSqlState(), k -> new ArrayList<>())
                    .add(deadlock.name());
            }
        }

        DeadlockSQLState(String sqlState) {
            this.sqlState = sqlState;
        }

        public String getSqlState() {
            return sqlState;
        }

        public static boolean isDeadlock(String sqlState) {
            return SQL_STATE_LOOKUP_MAP.containsKey(sqlState);
        }

        public static List<String> getVendorsForSqlState(String sqlState) {
            return SQL_STATE_LOOKUP_MAP.getOrDefault(sqlState, Collections.emptyList());
        }
    }

    public DatabaseDeadlockExceptionChecker() {
        registerDefaultDeadlockExceptions();
    }

    private void registerDefaultDeadlockExceptions() {
        addDeadlockException(SQLTransactionRollbackException.class);
    }


    public void addDeadlockException(Class<? extends Throwable> exceptionClass) {
        deadlockExceptions.add(exceptionClass);
    }

    public boolean isDeadlockException(Throwable e) {
        while (e != null) {
            if (e instanceof SQLException sqlEx) {
                String sqlState = sqlEx.getSQLState();
                if (DeadlockSQLState.isDeadlock(sqlState)) {
                    return true;
                }
            }
            e = e.getCause();
        }

        return false;
    }

    public DatabaseDeadlockEvent createDeadlockEvent(long timestamp, Throwable e) {
        return new DatabaseDeadlockEvent(
            timestamp,
            e.getClass().getSimpleName(),
            extractSqlState(e),
            e.getMessage()
        );
    }

    private String extractSqlState(Throwable e) {
        while (e != null) {
            if (e instanceof SQLException sqlEx) {
                return sqlEx.getSQLState();
            }
            e = e.getCause();
        }

        return "UNKNOWN";
    }
}
