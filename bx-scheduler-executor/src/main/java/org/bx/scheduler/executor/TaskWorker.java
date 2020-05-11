package org.bx.scheduler.executor;

import lombok.extern.slf4j.Slf4j;
import org.bx.scheculer.scheduler.entity.SchedulerContext;
import org.bx.scheduler.dispatcher.IDispatcher;
import org.bx.scheduler.engine.entity.SchedulerConfiguration;
import org.bx.scheduler.executor.entity.SchedulerExecutorContext;
import org.bx.scheduler.executor.stratege.ExecutorStrategeFactory;
import org.bx.scheduler.executor.stratege.IExecutorStratege;
import org.bx.scheduler.lock.ILog;
import org.bx.scheduler.lock.entity.SchedulerLogInfo;
import org.bx.scheduler.store.IExecutorDetailStore;
import org.bx.scheduler.store.IExecutorStore;
import org.bx.scheduler.store.IJobInfoStore;
import org.bx.scheduler.store.entity.SchedulerExecutorDetailIInfo;
import org.bx.scheduler.store.entity.SchedulerExecutorInfo;
import org.bx.scheduler.store.entity.SchedulerJobInfo;
import org.bx.scheduler.store.entity.SchedulerTriggerInfo;

import java.util.List;

@Slf4j
public class TaskWorker implements Runnable {
    private SchedulerContext schedulerContext;

    public TaskWorker(SchedulerContext schedulerContext) {
        this.schedulerContext = schedulerContext;
    }

    @Override
    public void run() {
        final SchedulerTriggerInfo triggerInfo = schedulerContext.getTriggerInfo();
        log.debug("receive task:{}", triggerInfo);
        final SchedulerConfiguration configuration = schedulerContext.getSchedulerInfo().getConfiguration();
        final SchedulerLogInfo logInfo = schedulerContext.getLogInfo();
        final IExecutorStore executorStore = configuration.getExecutorStore();
        final SchedulerExecutorInfo executorInfo = executorStore.getExecutorInfo(triggerInfo.getExecutorId());
        final ILog logger = configuration.getLogger();
        if (executorInfo == null) {
            logInfo.setMsg("executorInfo is null");
            logInfo.setStatus(SchedulerLogInfo.FAIL_STATUS);
        }
        final IJobInfoStore jobInfoStore = configuration.getJobInfoStore();
        final SchedulerJobInfo jobInfo = jobInfoStore.getJobInfo(triggerInfo.getId());
        if (jobInfo == null) {
            logInfo.setMsg("jobInfo is null");
            logInfo.setStatus(SchedulerLogInfo.FAIL_STATUS);
        }
        logInfo.setClassName(jobInfo.getClassName());
        final IExecutorDetailStore executorDetailStore = configuration.getExecutorDetailStore();
        final List<SchedulerExecutorDetailIInfo> executorDetailInfoList = executorDetailStore.getExecutorDetailInfo(executorInfo.getId());
        if (executorDetailInfoList.isEmpty()) {
            logInfo.setMsg("executorDetailInfo is null");
            logInfo.setStatus(SchedulerLogInfo.FAIL_STATUS);
        }
        logger.updateLog(logInfo);
        if (logInfo.getStatus() == SchedulerLogInfo.FAIL_STATUS) {
            log.debug("execute fail.log:{}", logInfo);
            return;
        }
        final IExecutorStratege stratege = ExecutorStrategeFactory.createStratege(triggerInfo.getStrategy());
        final List<SchedulerExecutorContext> schedulerExecutorContextList = stratege.buildSchedulerExecutorContext(triggerInfo, executorDetailInfoList, jobInfo);
        final IDispatcher dispatcher = configuration.getDispatcher();
        schedulerExecutorContextList.forEach(schedulerExecutorContext -> dispatcher.dispatch(schedulerExecutorContext));
    }


}