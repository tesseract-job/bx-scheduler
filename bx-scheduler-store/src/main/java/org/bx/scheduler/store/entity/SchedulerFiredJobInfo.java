package org.bx.scheduler.store.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author nickle
 * @since 2019-07-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SchedulerFiredJobInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String triggerId;

    private String triggerName;

    private String parentTriggerId;

    private String parentTriggerName;

    private String className;

    private String jobId;

    private String executorDetailId;

    private String socket;

    private Long createTime;

    private String logId;

    private Integer retryCount;

    private Integer shardingIndex;

    private String deptId;

    private String deptName;

    private String creator;

}
