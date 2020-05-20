package org.bx.scheduler.executor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.scheduler.entity.SchedulerContext;
import org.bx.scheduler.scheduler.entity.SchedulerInfo;
import org.bx.scheduler.dispatcher.ISchedulerServerDispatcher;
import org.bx.scheduler.common.bean.TaskExecuteInfo;
import org.bx.scheduler.common.util.BeanUtils;
import org.bx.scheduler.entity.SchedulerConfiguration;
import org.bx.scheduler.executor.entity.SchedulerExecutorContext;
import org.bx.scheduler.executor.stratege.ExecutorStrategeFactory;
import org.bx.scheduler.executor.stratege.ISchedulerServerExecutorStratege;
import org.bx.scheduler.executor.threadPool.ISchedulerThreadPool;
import org.bx.scheduler.log.entity.SchedulerLogInfo;
import org.bx.scheduler.store.entity.SchedulerExecutorDetailIInfo;
import org.bx.scheduler.store.entity.SchedulerTriggerInfo;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
@AllArgsConstructor
public class DefaultSchedulerServerExecutor implements ISchedulerServerExecutor {
    private ISchedulerThreadPool threadPool;
    private ExecutorService retryExecutorService;

    @Override
    public int scheduleNum() {
        return threadPool.getAvailableThreadNum();
    }

    @Override
    public void execute(SchedulerContext schedulerContext) {
        threadPool.execute(new TaskWorker(schedulerContext));
    }

    /**
     * 1、检测重试次数是否到达最大次数，如果到达直接返回
     * 2、如果未到达重试次数，新生成一个新的log
     * 3、将旧的log 部分属性copy到新log上，新的log重试次数+1
     * 4、重新获取一个新的执行机器（排除掉异常的机器）
     * 5、填充schedulerExecutorContext的ContextLogInfo、ExecutorDetailIInfo
     * 6、下发dispatcher执行
     *
     * @param schedulerExecutorContext 调度执行上下文
     */
    @Override
    public void taskRetry(SchedulerExecutorContext schedulerExecutorContext) {
        int curRetryCount = schedulerExecutorContext.getRetryCount();
        final SchedulerTriggerInfo triggerInfo = schedulerExecutorContext.getSchedulerContext().getTriggerInfo();
        final Integer retryNum = triggerInfo.getRetryCount();
        if (curRetryCount >= retryNum) {
            log.debug("retryTotal eq curRetryCount, don't retry");
            return;
        }
        schedulerExecutorContext.setRetryCount(curRetryCount++);
        final SchedulerConfiguration configuration = schedulerExecutorContext.getSchedulerContext().getSchedulerInfo().getConfiguration();
        SchedulerLogInfo contextLogInfo = schedulerExecutorContext.getContextLogInfo();
        final SchedulerLogInfo logInfo = SchedulerLogInfo.createDefaultSchedulerLogInfo(configuration.getIdGenerator());
        BeanUtils.propertyCopy(contextLogInfo, logInfo, "id", "createTime", "endTime", "retryCount");
        contextLogInfo = null;
        logInfo.setRetryCount(curRetryCount);
        final List<SchedulerExecutorDetailIInfo> executorDetailInfoList = configuration.
                getExecutorDetailStore().getExecutorDetailInfo(triggerInfo.getExecutorId(), schedulerExecutorContext.getExecutorDetailIInfo().getId());
        if (executorDetailInfoList.isEmpty()) {
            logInfo.setMsg("executorDetailInfo is null");
            logInfo.setStatus(SchedulerLogInfo.FAIL_STATUS);
        }
        configuration.getLogger().addLog(logInfo);
        if (logInfo.getStatus() == SchedulerLogInfo.FAIL_STATUS) {
            log.debug("execute fail.log:{}", logInfo);
            return;
        }
        schedulerExecutorContext.setContextLogInfo(logInfo);
        final ISchedulerServerExecutorStratege stratege = ExecutorStrategeFactory.createStratege(triggerInfo.getStrategy());
        final ISchedulerServerDispatcher dispatcher = configuration.getDispatcher();
        final SchedulerExecutorDetailIInfo schedulerExecutorDetailIInfo = stratege.selectExecutorDetail(executorDetailInfoList);
        schedulerExecutorContext.setExecutorDetailIInfo(schedulerExecutorDetailIInfo);
        dispatcher.dispatch(schedulerExecutorContext);
    }

    @Override
    public void taskRetry(TaskExecuteInfo executeInfo, SchedulerInfo schedulerInfo) {
        retryExecutorService.execute(new RetryWorker(executeInfo, schedulerInfo));
    }


}
