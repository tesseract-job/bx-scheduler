package org.bx.scheduler.lock;

import org.bx.scheduler.lock.entity.SchedulerLockInfo;

public interface IDistributeLock {
    /**
     * 根据传入lockInfo上锁
     *
     * @param lockInfo
     * @throws Exception
     */
    void lock(SchedulerLockInfo lockInfo) throws Exception;

    /**
     * 根据传入lockInfo解锁
     *
     * @param lockInfo
     * @throws Exception
     */
    void unLock(SchedulerLockInfo lockInfo) throws Exception;
}
