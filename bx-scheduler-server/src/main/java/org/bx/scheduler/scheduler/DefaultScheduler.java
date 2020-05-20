package org.bx.scheduler.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.bx.scheduler.common.lifecycle.AbstractLifecycle;
import org.bx.scheduler.entity.SchedulerConfiguration;
import org.bx.scheduler.executor.ISchedulerServerExecutor;
import org.bx.scheduler.log.IDistributeLock;
import org.bx.scheduler.log.mysql.MysqlFUDistributeLock;
import org.bx.scheduler.scheduler.entity.SchedulerContext;
import org.bx.scheduler.scheduler.entity.SchedulerInfo;
import org.bx.scheduler.store.ITriggerStore;
import org.bx.scheduler.store.entity.SchedulerDeptInfo;
import org.bx.scheduler.store.entity.SchedulerTriggerInfo;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Slf4j
public class DefaultScheduler extends AbstractLifecycle implements IScheduler {
    public static final String TRIGGER_LOCK_NAME = "BX-SCHEDULER-TRIGGER-LOCK";

    @Override
    public void schedule(SchedulerInfo schedulerInfo) throws Exception {
        log.debug("schedulerInfo:{}", schedulerInfo);
        final SchedulerDeptInfo deptInfo = schedulerInfo.getDeptInfo();
        final SchedulerConfiguration configuration = schedulerInfo.getConfiguration();
        final String key = TRIGGER_LOCK_NAME + "-" + schedulerInfo.getDeptInfo().getName();
        final IDistributeLock lock = new MysqlFUDistributeLock(configuration.getDataSource(), key);
        final ISchedulerServerExecutor executor = configuration.getExecutor();
        final ITriggerStore triggerStore = configuration.getTriggerStore();
        lock.lock();
        try {
            final List<SchedulerTriggerInfo> schedulerTriggerInfos = triggerStore.getTriggerInfoList(deptInfo.getId()
                    , caculateStartTime(configuration), caculateEndTime(configuration), executor.scheduleNum());
            schedulerTriggerInfos.forEach(triggerInfo -> {
                //更新下一次執行時間
                //构建cron计算器
                CronExpression cronExpression;
                try {
                    cronExpression = new CronExpression(triggerInfo.getCron());
                } catch (ParseException e) {
                    log.error("创建CronExpression出错:{},异常信息:{}", triggerInfo, e.getMessage());
                    return;
                }
                SchedulerTriggerInfo updateTrigger = new SchedulerTriggerInfo();
                updateTrigger.setId(triggerInfo.getId());
                updateTrigger.setNextTriggerTime(cronExpression.getTimeAfter(new Date()).getTime());
                updateTrigger.setPrevTriggerTime(System.currentTimeMillis());
                triggerStore.updateSchedulerTriggerInfo(updateTrigger);
                executor.execute(SchedulerContext.createSchedulerContext(configuration, schedulerInfo, triggerInfo));
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private long caculateStartTime(SchedulerConfiguration configuration) {
        return System.currentTimeMillis() - configuration.getScheduleTime();
    }

    private long caculateEndTime(SchedulerConfiguration configuration) {
        return System.currentTimeMillis() + configuration.getPreReadTime();
    }
}
