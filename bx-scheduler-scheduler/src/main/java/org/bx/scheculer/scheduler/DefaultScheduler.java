package org.bx.scheculer.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.bx.scheculer.scheduler.entity.SchedulerInfo;
import org.bx.scheduler.common.lifecycle.AbstractLifecycle;
import org.bx.scheduler.engine.entity.SchedulerConfiguration;
import org.bx.scheduler.executor.IExecutor;
import org.bx.scheduler.lock.IDistributeLock;
import org.bx.scheduler.lock.entity.SchedulerLockInfo;
import org.bx.scheduler.store.ITriggerStore;
import org.bx.scheduler.store.entity.SchedulerDeptInfo;
import org.bx.scheduler.store.entity.SchedulerTriggerInfo;

import java.util.List;

import static org.bx.scheduler.common.util.CommonUtis.createSchedulerContext;

@Slf4j
public class DefaultScheduler extends AbstractLifecycle implements IScheduler {
    public static final String TRIGGER_LOCK_NAME = "BX-SCHEDULER-TRIGGER-LOCK";

    @Override
    public void schedule(SchedulerInfo schedulerInfo) throws Exception {
        log.debug("schedulerInfo:{}", schedulerInfo);
        final SchedulerDeptInfo deptInfo = schedulerInfo.getDeptInfo();
        final SchedulerConfiguration configuration = schedulerInfo.getConfiguration();
        final IDistributeLock lock = configuration.getLock();
        final IExecutor executor = configuration.getExecutor();
        final ITriggerStore triggerStore = configuration.getTriggerStore();
        final SchedulerLockInfo schedulerLockInfo = new SchedulerLockInfo();
        schedulerLockInfo.setIdentity(TRIGGER_LOCK_NAME);
        schedulerLockInfo.setIdentity(deptInfo.getName());
        lock.lock(schedulerLockInfo);
        try {
            final List<SchedulerTriggerInfo> schedulerTriggerInfos = triggerStore.getTriggerInfoList(deptInfo.getId()
                    , caculateStartTime(configuration), caculateEndTime(configuration), executor.scheduleNum());
            schedulerTriggerInfos.forEach(triggerInfo -> executor.execute(createSchedulerContext(configuration, schedulerInfo, triggerInfo)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unLock(schedulerLockInfo);
        }
    }

    private long caculateStartTime(SchedulerConfiguration configuration) {
        return System.currentTimeMillis() - configuration.getScheduleTime();
    }

    private long caculateEndTime(SchedulerConfiguration configuration) {
        return System.currentTimeMillis() + configuration.getPreReadTime();
    }
}
