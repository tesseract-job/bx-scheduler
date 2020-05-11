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
 * @since 2019-07-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SchedulerTriggerInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String parentTriggerId;
    private String parentTriggerName;
    private Long nextTriggerTime;
    private Long prevTriggerTime;
    private String cron;
    private Integer strategy;
    private Integer shardingNum;
    private Integer retryCount;
    private String description;
    private String executeParam;
    private String deptName;
    private String deptId;
    private Integer status;
    private String executorId;
    private String executorName;
    private String creator;
    private Long createTime;
    private Long updateTime;
}
