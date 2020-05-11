package org.bx.scheduler.executor;

import org.bx.scheculer.scheduler.entity.SchedulerInfo;
import org.bx.scheduler.common.bean.TaskExecuteInfo;
import org.bx.scheduler.common.util.CommonUtis;
import org.bx.scheduler.engine.entity.SchedulerConfiguration;
import org.bx.scheduler.executor.entity.SchedulerExecutorContext;
import org.bx.scheduler.lock.ILog;
import org.bx.scheduler.lock.entity.SchedulerLogInfo;
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
        executorContext.setSchedulerContext(CommonUtis.createSchedulerContext(configuration, schedulerInfo, triggerInfo));
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
