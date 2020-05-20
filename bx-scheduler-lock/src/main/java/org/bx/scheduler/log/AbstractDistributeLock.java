package org.bx.scheduler.log;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class AbstractDistributeLock implements IDistributeLock {
    protected String lockName;

    public AbstractDistributeLock(String lockName) {
        this.lockName = lockName;
    }

    @Override
    public String lockName() {
        return this.lockName;
    }

    @Override
    public void lock() {
        throw new RuntimeException("not support method");
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new RuntimeException("not support method");
    }

    @Override
    public boolean tryLock() {
        throw new RuntimeException("not support method");
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new RuntimeException("not support method");
    }

    @Override
    public void unlock() {
        throw new RuntimeException("not support method");
    }

    @Override
    public Condition newCondition() {
        throw new RuntimeException("not support method");
    }
}
