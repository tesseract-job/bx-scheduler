package org.bx.scheduler.engine.entity;

import lombok.Data;
import org.bx.scheculer.scheduler.IScheduler;
import org.bx.scheduler.client.IClientPool;
import org.bx.scheduler.client.ISchedulerServerDispatcher;
import org.bx.scheduler.common.serializer.ISerializer;
import org.bx.scheduler.executor.ISchedulerServerExecutor;
import org.bx.scheduler.executor.threadPool.ISchedulerThreadPool;
import org.bx.scheduler.idgenerator.IDGenerator;
import org.bx.scheduler.lock.IDistributeLock;
import org.bx.scheduler.lock.ILog;
import org.bx.scheduler.store.*;

@Data
public class SchedulerConfiguration {
    private int scheduleTime = 20 * 1000;
    private int preReadTime = 15 * 1000;
    private ITriggerStore triggerStore;
    private IDistributeLock lock;
    private ISchedulerServerExecutor executor;
    private ILog logger;
    private IDGenerator idGenerator;
    private ISchedulerThreadPool threadPool;
    private IExecutorStore executorStore;
    private IExecutorDetailStore executorDetailStore;
    private IJobInfoStore jobInfoStore;
    private ISchedulerServerDispatcher dispatcher;
    private IClientPool clientPool;
    private IFiredJobInfoStore firedJobInfoStore;
    private IScheduler scheduler;
    private ISerializer serializer;
    private int engineServerPort;
}
