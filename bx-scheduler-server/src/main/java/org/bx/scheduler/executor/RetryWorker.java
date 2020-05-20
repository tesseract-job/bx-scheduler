package org.bx.scheduler.executor;

import org.bx.scheduler.scheduler.entity.SchedulerContext;
import org.bx.scheduler.scheduler.entity.SchedulerInfo;
import org.bx.scheduler.common.bean.TaskExecuteInfo;
import org.bx.scheduler.entity.SchedulerConfiguration;
import org.bx.scheduler.executor.entity.SchedulerExecutorContext;
import org.bx.scheduler.log.ILog;
import org.bx.scheduler.log.entity.SchedulerLogInfo;
import org.bx.scheduler.store.ITriggerStore;
import org.bx.scheduler.store.entity.SchedulerExecutorDetailIInfo;
import org.bx.scheduler.store.entity.SchedulerJobInfo;
import org.bx.scheduler.store.entity.SchedulerTriggerInfo;

public class RetryWorker implements Runnable {
    private TaskExecuteInfo executeInfo;
    private SchedulerInfo schedulerInfo;

    public RetryWorker(TaskExecuteInfo executeInfo, SchedulerInfo schedulerInfo) {
        this.executeInfo = executeInfo;
        this.schedulerInfo = schedulerInfo;
    }

    @Override
    public void run() {
        final SchedulerConfiguration configuration = schedulerInfo.getConfiguration();
        final ILog logger = configuration.getLogger();
        final ITriggerStore triggerStore = configuration.getTriggerStore();
        final SchedulerLogInfo schedulerLogInfo = logger.getLogById(executeInfo.getLogId());
        if (schedulerLogInfo == null) {
            throw new RuntimeException("schedulerLogInfo is null,stop retry");
        }
        final SchedulerTriggerInfo triggerInfo = triggerStore.getSchedulerTriggerInfoById(schedulerLogInfo.getTriggerId());
        if (triggerInfo == null) {
            throw new RuntimeException("triggerInfo is null,stop retry");
        }
        final SchedulerJobInfo jobInfo = configuration.getJobInfoStore().getJobInfo(triggerInfo.getId());
        if (jobInfo == null) {
            throw new RuntimeException("jobInfo is null,stop retry");
        }
        final SchedulerExecutorContext executorContext = new SchedulerExecutorContext();
        executorContext.setSchedulerContext(SchedulerContext.createSchedulerContext(configuration, schedulerInfo, triggerInfo));
        final SchedulerExecutorDetailIInfo executorDetailIInfo = new SchedulerExecutorDetailIInfo();
        executorDetailIInfo.setId(schedulerLogInfo.getExecutorDetailId());
        /**
         * 由于过滤只需要ID 这里直接生成一个
         */
        executorContext.setExecutorDetailIInfo(executorDetailIInfo);
        executorContext.setRetryCount(schedulerLogInfo.getRetryCount());
        executorContext.setContextLogInfo(schedulerLogInfo);
        executorContext.setJobInfo(jobInfo);
        executorContext.setShardingIndex(executeInfo.getShardingIndex());
        configuration.getExecutor().taskRetry(executorContext);
    }
}
