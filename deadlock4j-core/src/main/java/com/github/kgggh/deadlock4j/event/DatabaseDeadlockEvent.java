package com.github.kgggh.deadlock4j.event;

public class DatabaseDeadlockEvent extends DeadlockEvent {
    private final String exceptionName;
    private final String sqlState;
    private final String reason;

    public DatabaseDeadlockEvent(long timestamp, String exceptionName, String sqlState, String reason) {
        super(DeadlockType.DATABASE, timestamp);
        this.exceptionName = exceptionName;
        this.sqlState = sqlState;
        this.reason = reason;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public String getSqlState() {
        return sqlState;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "DatabaseDeadlockEvent{" +
            "exceptionName='" + exceptionName + '\'' +
            ", sqlState='" + sqlState + '\'' +
            ", reason='" + reason + '\'' +
            '}';
    }
}
