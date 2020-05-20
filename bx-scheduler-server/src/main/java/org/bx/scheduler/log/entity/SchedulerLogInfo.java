package org.bx.scheduler.log.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.bx.scheduler.idgenerator.IDGenerator;
import org.bx.scheduler.idgenerator.entity.IDGeneratorContext;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author nickle
 * @since 2019-07-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SchedulerLogInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String SCHEDULER_LOG_NAME = "Scheduler-Log";
    public static final int DEFAULT_STATUS = 0;
    public static final int FAIL_STATUS = 1;
    public static final int SUCCESS_STATUS = 2;

    private String id;

    private String className;

    private String triggerId;

    private String triggerName;

    private String socket;

    private Integer status;

    private String msg;

    private String creator;

    private Long createTime;

    private Long endTime;

    private String deptName;

    private String deptId;

    private String executorDetailId;

    private int strategy;

    private Integer shardingIndex;

    private Integer retryCount;

    public static SchedulerLogInfo createDefaultSchedulerLogInfo(IDGenerator idGenerator) {
        final SchedulerLogInfo schedulerLogInfo = new SchedulerLogInfo();
        schedulerLogInfo.setCreateTime(System.currentTimeMillis());
        final IDGeneratorContext idGeneratorContext = new IDGeneratorContext();
        idGeneratorContext.setBusinessName(SCHEDULER_LOG_NAME);
        schedulerLogInfo.setId(idGenerator.generate(idGeneratorContext));
        schedulerLogInfo.setStatus(DEFAULT_STATUS);
        return schedulerLogInfo;
    }
}
