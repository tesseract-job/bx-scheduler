package org.bx.scheduler.entity;

import lombok.Data;
import org.bx.scheduler.common.serializer.ISerializer;
import org.bx.scheduler.dispatcher.IClientPool;
import org.bx.scheduler.dispatcher.ISchedulerServerDispatcher;
import org.bx.scheduler.executor.ISchedulerServerExecutor;
import org.bx.scheduler.executor.threadPool.ISchedulerThreadPool;
import org.bx.scheduler.idgenerator.IDGenerator;
import org.bx.scheduler.log.ILog;
import org.bx.scheduler.scheduler.IScheduler;
import org.bx.scheduler.store.*;

import javax.sql.DataSource;

@Data
public class SchedulerConfiguration {
    private int scheduleTime = 20 * 1000;
    private int preReadTime = 15 * 1000;
    private ITriggerStore triggerStore;
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
    private DataSource dataSource;
}
