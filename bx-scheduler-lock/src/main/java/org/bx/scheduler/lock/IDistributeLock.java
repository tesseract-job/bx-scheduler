package org.bx.scheduler.lock;

import java.util.concurrent.TimeUnit;

public interface IDistributeLock {
    /**
     * 根据传入key上锁
     * 阻塞式
     *
     * @param key
     * @throws Exception
     */
    void lock(String key) throws Exception;


    /**
     * 根据传入key上锁
     * 非阻塞式
     *
     * @param key
     * @throws Exception
     */
    boolean trylock(String key) throws Exception;

    boolean tryLock(String key, long time, TimeUnit unit) throws Exception;

    /**
     * 根据传入key解锁
     *
     * @param key
     * @throws Exception
     */
    void unLock(String key) throws Exception;
}
