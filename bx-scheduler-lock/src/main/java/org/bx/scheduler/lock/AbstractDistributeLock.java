package org.bx.scheduler.lock;

import org.bx.scheduler.lock.entity.SchedulerLockInfo;

import java.util.concurrent.TimeUnit;

public abstract class AbstractDistributeLock implements IDistributeLock {
    @Override
    public void lock(SchedulerLockInfo lockInfo) throws Exception {
        throw new RuntimeException("not support method");
    }

    @Override
    public boolean trylock(SchedulerLockInfo lockInfo) throws Exception {
        throw new RuntimeException("not support method");
    }

    @Override
    public boolean tryLock(SchedulerLockInfo lockInfo, long time, TimeUnit unit) throws Exception {
        throw new RuntimeException("not support method");
    }

    @Override
    public void unLock(SchedulerLockInfo lockInfo) throws Exception {
        throw new RuntimeException("not support method");
    }
}
