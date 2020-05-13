package org.bx.scheduler.lock;

import java.util.concurrent.TimeUnit;

public abstract class AbstractDistributeLock implements IDistributeLock {
    @Override
    public void lock(String key) throws Exception {
        throw new RuntimeException("not support method");
    }

    @Override
    public boolean trylock(String key) throws Exception {
        throw new RuntimeException("not support method");
    }

    @Override
    public boolean tryLock(String key, long time, TimeUnit unit) throws Exception {
        throw new RuntimeException("not support method");
    }

    @Override
    public void unLock(String key) throws Exception {
        throw new RuntimeException("not support method");
    }
}
