package org.bx.scheduler.dispatcher;

import lombok.extern.slf4j.Slf4j;
import org.bx.scheculer.scheduler.entity.SchedulerContext;
import org.bx.scheculer.scheduler.entity.SchedulerInfo;
import org.bx.scheduler.client.IClientPool;
import org.bx.scheduler.client.IDispatcherClient;
import org.bx.scheduler.common.bean.TaskExecuteInfo;
import org.bx.scheduler.engine.entity.SchedulerConfiguration;
import org.bx.scheduler.executor.entity.SchedulerExecutorContext;
import org.bx.scheduler.lock.entity.SchedulerLogInfo;
import org.bx.scheduler.store.IFiredJobInfoStore;
import org.bx.scheduler.store.entity.SchedulerExecutorDetailIInfo;
import org.bx.scheduler.store.entity.SchedulerFiredJobInfo;
import org.bx.scheduler.store.entity.SchedulerJobInfo;
import org.bx.scheduler.store.entity.SchedulerTriggerInfo;

@Slf4j
public class DefaultDispatcher implements IDispatcher {
    @Override
    public void dispatch(SchedulerExecutorContext executorContext) {
        final SchedulerConfiguration configuration = executorContext.getSchedulerContext().getSchedulerInfo().getConfiguration();
        final SchedulerLogInfo logInfo = executorContext.getContextLogInfo();
        final SchedulerExecutorDetailIInfo executorDetailIInfo = executorContext.getExecutorDetailIInfo();
        try {
            final IClientPool clientPool = configuration.getClientPool();
            final IDispatcherClient client = clientPool.getClient(executorDetailIInfo.getSocket());
            final TaskExecuteInfo taskRequestInfo = new TaskExecuteInfo();
            client.request(taskRequestInfo);
            saveFiredTrigger(executorContext);
        } catch (Exception e) {
            String msg = "dispatch fail";
            log.error(msg, e);
            logInfo.setMsg(msg);
            logInfo.setStatus(SchedulerLogInfo.FAIL_STATUS);
            logInfo.setEndTime(System.currentTimeMillis());
            configuration.getLogger().updateLog(logInfo);
            configuration.getExecutor().taskRetry(executorContext);
        }
    }

    private void saveFiredTrigger(SchedulerExecutorContext executorContext) {
        final SchedulerContext schedulerContext = executorContext.getSchedulerContext();
        final SchedulerInfo schedulerInfo = schedulerContext.getSchedulerInfo();
        final SchedulerConfiguration configuration = schedulerInfo.getConfiguration();
        final SchedulerFiredJobInfo schedulerFiredJobInfo = new SchedulerFiredJobInfo();
        final SchedulerJobInfo jobInfo = executorContext.getJobInfo();
        final SchedulerLogInfo contextLogInfo = executorContext.getContextLogInfo();
        final SchedulerTriggerInfo triggerInfo = schedulerContext.getTriggerInfo();
        final SchedulerExecutorDetailIInfo executorDetailIInfo = executorContext.getExecutorDetailIInfo();
        final IFiredJobInfoStore firedJobInfoStore = configuration.getFiredJobInfoStore();
        schedulerFiredJobInfo.setClassName(jobInfo.getClassName());
        schedulerFiredJobInfo.setCreateTime(System.currentTimeMillis());
        schedulerFiredJobInfo.setDeptId(triggerInfo.getDeptId());
        schedulerFiredJobInfo.setDeptName(triggerInfo.getDeptName());
        schedulerFiredJobInfo.setJobId(jobInfo.getId());
        schedulerFiredJobInfo.setLogId(contextLogInfo.getId());
        schedulerFiredJobInfo.setSocket(executorDetailIInfo.getSocket());
        schedulerFiredJobInfo.setShardingIndex(executorContext.getShardingIndex());
        schedulerFiredJobInfo.setParentTriggerId(triggerInfo.getParentTriggerId());
        schedulerFiredJobInfo.setParentTriggerName(triggerInfo.getParentTriggerName());
        schedulerFiredJobInfo.setRetryCount(executorContext.getRetryCount());
        schedulerFiredJobInfo.setTriggerId(triggerInfo.getId());
        schedulerFiredJobInfo.setTriggerName(triggerInfo.getName());
        firedJobInfoStore.addFiredJobInfo(schedulerFiredJobInfo);
    }
}
