package org.bx.scheduler.executor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bx.scheculer.scheduler.entity.SchedulerContext;
import org.bx.scheculer.scheduler.entity.SchedulerInfo;
import org.bx.scheduler.common.bean.TaskExecuteInfo;
import org.bx.scheduler.common.util.BeanUtils;
import org.bx.scheduler.dispatcher.IDispatcher;
import org.bx.scheduler.engine.entity.SchedulerConfiguration;
import org.bx.scheduler.executor.entity.SchedulerExecutorContext;
import org.bx.scheduler.executor.stratege.ExecutorStrategeFactory;
import org.bx.scheduler.executor.stratege.IExecutorStratege;
import org.bx.scheduler.executor.threadPool.ISchedulerThreadPool;
import org.bx.scheduler.lock.entity.SchedulerLogInfo;
import org.bx.scheduler.store.entity.SchedulerExecutorDetailIInfo;
import org.bx.scheduler.store.entity.SchedulerTriggerInfo;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
@AllArgsConstructor
public class DefaultExecutor implements IExecutor {
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

    @Override
    public void taskRetry(SchedulerExecutorContext schedulerExecutorContext) {
        int curRetryCount = schedulerExecutorContext.getRetryCount();
        final SchedulerTriggerInfo triggerInfo = schedulerExecutorContext.getSchedulerContext().getTriggerInfo();
        final Integer retryTotal = triggerInfo.getRetryCount();
        if (curRetryCount >= retryTotal) {
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
        final IExecutorStratege stratege = ExecutorStrategeFactory.createStratege(triggerInfo.getStrategy());
        final IDispatcher dispatcher = configuration.getDispatcher();
        final SchedulerExecutorDetailIInfo schedulerExecutorDetailIInfo = stratege.selectExecutorDetail(executorDetailInfoList);
        schedulerExecutorContext.setExecutorDetailIInfo(schedulerExecutorDetailIInfo);
        dispatcher.dispatch(schedulerExecutorContext);
    }

    @Override
    public void taskRetry(TaskExecuteInfo executeInfo, SchedulerInfo schedulerInfo) {
        retryExecutorService.execute(new RetryWorker(executeInfo, schedulerInfo));
    }


}
