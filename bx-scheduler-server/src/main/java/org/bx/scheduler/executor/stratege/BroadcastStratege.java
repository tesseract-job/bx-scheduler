package org.bx.scheduler.executor.stratege;

import com.google.common.collect.Lists;
import org.bx.scheduler.scheduler.entity.SchedulerContext;
import org.bx.scheduler.scheduler.entity.SchedulerInfo;
import org.bx.scheduler.common.util.BeanUtils;
import org.bx.scheduler.executor.entity.SchedulerExecutorContext;
import org.bx.scheduler.log.entity.SchedulerLogInfo;
import org.bx.scheduler.store.entity.SchedulerExecutorDetailIInfo;
import org.bx.scheduler.store.entity.SchedulerJobInfo;
import org.bx.scheduler.store.entity.SchedulerTriggerInfo;

import java.util.List;

/**
 * 广播执行，每台机器均执行
 */
public class BroadcastStratege extends AbstractSchedulerServerExecutorStratege implements ISchedulerServerExecutorStratege {
    @Override
    public List<SchedulerExecutorContext> buildSchedulerExecutorContext(SchedulerContext schedulerContext,
                                                                        SchedulerTriggerInfo triggerInfo,
                                                                        List<SchedulerExecutorDetailIInfo> executorDetailIInfoList,
                                                                        SchedulerJobInfo jobInfo) {
        final List<SchedulerExecutorContext> contextArrayList = Lists.newArrayListWithCapacity(executorDetailIInfoList.size());
        final SchedulerInfo schedulerInfo = schedulerContext.getSchedulerInfo();
        int size = executorDetailIInfoList.size();
        for (int i = 0; i < size; i++) {
            final SchedulerExecutorDetailIInfo schedulerExecutorDetailIInfo = executorDetailIInfoList.get(i);
            final SchedulerExecutorContext schedulerExecutorContext = new SchedulerExecutorContext();
            fillSchedulerExecutorContext(schedulerExecutorContext, schedulerContext, jobInfo);
            schedulerExecutorContext.setExecutorDetailIInfo(schedulerExecutorDetailIInfo);
            SchedulerLogInfo contextLogInfo = SchedulerLogInfo.createDefaultSchedulerLogInfo(schedulerInfo.getConfiguration().getIdGenerator());
            BeanUtils.propertyCopy(contextLogInfo, schedulerContext.getLogInfo(), "id", "createTime", "endTime", "shardingIndex");
            contextLogInfo.setExecutorDetailId(schedulerExecutorDetailIInfo.getExecutorId());
            schedulerExecutorContext.setContextLogInfo(contextLogInfo);
            contextArrayList.add(schedulerExecutorContext);
        }
        return contextArrayList;
    }

}
