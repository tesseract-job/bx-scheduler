package org.bx.scheduler.lock;

import org.bx.scheduler.lock.entity.SchedulerLockInfo;

import java.util.concurrent.TimeUnit;

public interface IDistributeLock {
    /**
     * 根据传入lockInfo上锁
     * 阻塞式
     *
     * @param lockInfo
     * @throws Exception
     */
    void lock(SchedulerLockInfo lockInfo) throws Exception;


    /**
     * 根据传入lockInfo上锁
     * 非阻塞式
     *
     * @param lockInfo
     * @throws Exception
     */
    boolean trylock(SchedulerLockInfo lockInfo) throws Exception;

    boolean tryLock(SchedulerLockInfo lockInfo, long time, TimeUnit unit) throws Exception;

    /**
     * 根据传入lockInfo解锁
     *
     * @param lockInfo
     * @throws Exception
     */
    void unLock(SchedulerLockInfo lockInfo) throws Exception;
}
