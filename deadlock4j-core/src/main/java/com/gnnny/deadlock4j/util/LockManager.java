package com.gnnny.deadlock4j.util;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class LockManager {
    public enum LockCategory { THREAD, DATABASE }
    private final EnumMap<LockCategory, ReentrantLock> lockMap = new EnumMap<>(LockCategory.class);

    public LockManager() {
        lockMap.put(LockCategory.THREAD, new ReentrantLock());
        lockMap.put(LockCategory.DATABASE, new ReentrantLock());
    }

    public void lock(LockCategory category) {
        ReentrantLock lock = lockMap.get(category);
        if (lock == null) {
            throw new IllegalArgumentException("Invalid lock category: " + category);
        }

        lock.lock();
    }

    public void unlock(LockCategory category) {
        ReentrantLock lock = lockMap.get(category);
        if (lock == null) {
            throw new IllegalArgumentException("Invalid lock category: " + category);
        }

        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        } else {
            throw new IllegalStateException("The lock is not held by the current thread.");
        }
    }

    public Map<LockCategory, ReentrantLock> getLockMap() {
        return lockMap;
    }
}
