package com.gnnny.deadlock4j.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LockManagerTest {

    private LockManager lockManager;

    @BeforeEach
    void setUp() {
        lockManager = new LockManager();
    }

    @Test
    void testLock_ValidCategory_ShouldLockSuccessfully() {
        // given
        LockManager.LockCategory category = LockManager.LockCategory.THREAD;

        // when
        lockManager.lock(category);

        // then
        assertThat(lockManager.getLockMap().get(category).isLocked()).isTrue();
    }

    @Test
    void testUnlock_ValidCategory_ShouldUnlockSuccessfully() {
        // given
        LockManager.LockCategory category = LockManager.LockCategory.THREAD;
        lockManager.lock(category);

        // when
        lockManager.unlock(category);

        // then
        assertThat(lockManager.getLockMap().get(category).isLocked()).isFalse();
    }

    @Test
    void testLock_InvalidCategory_ShouldThrowException() {
        // given
        LockManager.LockCategory category = null;

        // when & then
        assertThatThrownBy(() -> lockManager.lock(category))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid lock category: null");
    }

    @Test
    void testUnlock_InvalidCategory_ShouldThrowException() {
        // given
        LockManager.LockCategory category = null;

        // when
        // then
        assertThatThrownBy(() -> lockManager.unlock(category))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid lock category: null");
    }

    @Test
    void testUnlock_LockNotHeldByCurrentThread_ShouldThrowException() {
        // given
        lockManager.lock(LockManager.LockCategory.THREAD);
        LockManager anotherLockManager = new LockManager();

        // when
        // then
        assertThatThrownBy(() -> anotherLockManager.unlock(LockManager.LockCategory.THREAD))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("The lock is not held by the current thread.");
    }

    @Test
    void testLockMultipleCategories_ShouldLockEachCategoryIndependently() {
        // given
        lockManager.lock(LockManager.LockCategory.THREAD);
        lockManager.lock(LockManager.LockCategory.DATABASE);

        // when
        boolean threadLocked = lockManager.getLockMap().get(LockManager.LockCategory.THREAD).isLocked();
        boolean dbLocked = lockManager.getLockMap().get(LockManager.LockCategory.DATABASE).isLocked();

        // then
        assertThat(threadLocked).isTrue();
        assertThat(dbLocked).isTrue();

        lockManager.unlock(LockManager.LockCategory.THREAD);
        lockManager.unlock(LockManager.LockCategory.DATABASE);
    }
}
