package org.bx.scheduler.executor.stratege;

import com.google.common.collect.Lists;
import org.bx.scheculer.scheduler.entity.SchedulerContext;
import org.bx.scheculer.scheduler.entity.SchedulerInfo;
import org.bx.scheduler.common.util.BeanUtils;
import org.bx.scheduler.executor.entity.SchedulerExecutorContext;
import org.bx.scheduler.lock.entity.SchedulerLogInfo;
import org.bx.scheduler.store.entity.SchedulerExecutorDetailIInfo;
import org.bx.scheduler.store.entity.SchedulerJobInfo;
import org.bx.scheduler.store.entity.SchedulerTriggerInfo;

import java.util.List;

/**
 * 一个机器一个分片index，从0开始
 */
public class ShardingStratege extends AbstractSchedulerServerExecutorStratege implements ISchedulerServerExecutorStratege {
    @Override
    public List<SchedulerExecutorContext> buildSchedulerExecutorContext(SchedulerContext schedulerContext,
                                                                        SchedulerTriggerInfo triggerInfo,
                                                                        List<SchedulerExecutorDetailIInfo> executorDetailIInfoList,
                                                                        SchedulerJobInfo jobInfo) {
        final List<SchedulerExecutorContext> contextArrayList = Lists.newArrayListWithCapacity(executorDetailIInfoList.size());
        int shardingNum = triggerInfo.getShardingNum();
        if (shardingNum == 0) {
            throw new RuntimeException("分片数不能等于0");
        }
        final SchedulerInfo schedulerInfo = schedulerContext.getSchedulerInfo();
        int size = executorDetailIInfoList.size();
        int count = 0;
        for (int i = 0; i < shardingNum; i++) {
            final SchedulerExecutorContext schedulerExecutorContext = new SchedulerExecutorContext();
            fillSchedulerExecutorContext(schedulerExecutorContext, schedulerContext, jobInfo);
            //轮询发送给执行器执行
            if (count >= size) {
                count = 0;
            }
            final SchedulerExecutorDetailIInfo schedulerExecutorDetailIInfo = executorDetailIInfoList.get(count);
            schedulerExecutorContext.setExecutorDetailIInfo(schedulerExecutorDetailIInfo);
            SchedulerLogInfo contextLogInfo = SchedulerLogInfo.createDefaultSchedulerLogInfo(schedulerInfo.getConfiguration().getIdGenerator());
            BeanUtils.propertyCopy(contextLogInfo, schedulerContext.getLogInfo(), "id", "createTime", "endTime", "shardingIndex");
            schedulerExecutorContext.setShardingIndex(count);
            contextLogInfo.setShardingIndex(count);
            contextLogInfo.setExecutorDetailId(schedulerExecutorDetailIInfo.getExecutorId());
            schedulerExecutorContext.setContextLogInfo(contextLogInfo);
            contextArrayList.add(schedulerExecutorContext);
            count++;

        }
        return contextArrayList;
    }

}
