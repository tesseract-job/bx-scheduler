package org.bx.scheduler.dispatcher;

import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.common.bean.TaskExecuteInfo;
import org.bx.scheduler.dispatcher.entity.DispatchContext;
import org.bx.scheduler.engine.entity.SchedulerConfiguration;
import org.bx.scheduler.executor.entity.SchedulerExecutorContext;
import org.bx.scheduler.log.entity.SchedulerLogInfo;
import org.bx.scheduler.scheduler.entity.SchedulerContext;
import org.bx.scheduler.scheduler.entity.SchedulerInfo;
import org.bx.scheduler.store.IFiredJobInfoStore;
import org.bx.scheduler.store.entity.SchedulerExecutorDetailIInfo;
import org.bx.scheduler.store.entity.SchedulerFiredJobInfo;
import org.bx.scheduler.store.entity.SchedulerJobInfo;
import org.bx.scheduler.store.entity.SchedulerTriggerInfo;


@Slf4j
public class DefaultSchedulerServerDispatcher implements ISchedulerServerDispatcher {
    /**
     * 1、获取到客户端连接池
     * 2、根据executorDetailIInfo里的执行机器的套接字地址从连接池里获取到IDispatcherClient
     * 3、构建TaskExecuteInfo下发任务
     * 4、保存任务到正在执行的触发器store里
     * 异常处理：
     * 1、记录日志异常信息
     * 2、更新日志信息
     * 3、执行任务重试
     *
     * @param executorContext 执行上下文
     */
    @Override
    public void dispatch(SchedulerExecutorContext executorContext) {
        final SchedulerConfiguration configuration = executorContext.getSchedulerContext().getSchedulerInfo().getConfiguration();
        final SchedulerLogInfo logInfo = executorContext.getContextLogInfo();
        final SchedulerExecutorDetailIInfo executorDetailIInfo = executorContext.getExecutorDetailIInfo();
        try {
            final IClientPool clientPool = configuration.getClientPool();
            final IDispatcherClient client = clientPool.getClient(executorDetailIInfo.getSocket());
            final TaskExecuteInfo taskRequestInfo = new TaskExecuteInfo();
            final DispatchContext dispatchContext = new DispatchContext();
            dispatchContext.setExecuteInfo(taskRequestInfo);
            dispatchContext.setConfiguration(configuration);
            client.request(dispatchContext);
            saveFiredTrigger(executorContext);
        } catch (Exception e) {
            log.error("dispatch fail", e);
            logInfo.setMsg(e.getMessage());
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
