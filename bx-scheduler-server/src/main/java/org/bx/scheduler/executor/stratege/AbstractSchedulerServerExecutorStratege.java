package org.bx.scheduler.executor.stratege;

import org.bx.scheduler.scheduler.entity.SchedulerContext;
import org.bx.scheduler.executor.entity.SchedulerExecutorContext;
import org.bx.scheduler.store.entity.SchedulerExecutorDetailIInfo;
import org.bx.scheduler.store.entity.SchedulerJobInfo;

import java.util.List;

public abstract class AbstractSchedulerServerExecutorStratege implements ISchedulerServerExecutorStratege {

    @Override
    public SchedulerExecutorDetailIInfo selectExecutorDetail(List<SchedulerExecutorDetailIInfo> executorDetailIInfoList) {
        return executorDetailIInfoList.get((int) (hash(executorDetailIInfoList) % executorDetailIInfoList.size()));
    }

    protected void fillSchedulerExecutorContext(SchedulerExecutorContext executorContext, SchedulerContext schedulerContext, SchedulerJobInfo jobInfo) {
        executorContext.setSchedulerContext(schedulerContext);
        executorContext.setJobInfo(jobInfo);
    }

    private long hash(Object object) {
        int h;
        return (object == null) ? 0 : (h = object.hashCode()) ^ (h >>> 16);
    }
}
