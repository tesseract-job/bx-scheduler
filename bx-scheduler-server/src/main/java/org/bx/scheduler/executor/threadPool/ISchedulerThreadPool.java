package org.bx.scheduler.executor.threadPool;

import java.util.concurrent.ExecutorService;

public interface ISchedulerThreadPool extends ExecutorService {
    int getAvailableThreadNum();
}
