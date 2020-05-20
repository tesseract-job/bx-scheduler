package org.bx.scheduler.log;

import java.util.concurrent.locks.Lock;

public interface IDistributeLock extends Lock {
    String lockName();
}
