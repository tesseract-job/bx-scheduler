package org.bx.scheduler.lock;

import java.util.concurrent.locks.Lock;

public interface IDistributeLock extends Lock {
    String lockName();
}
