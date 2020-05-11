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
 * @since 2019-07-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SchedulerExecutorDetailIInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String executorId;

    private String socket;

    private Double loadFactor;

    private Long createTime;

    private Long updateTime;

    /**
     * 冗余存储方便分析
     */
    private String deptName;

    private String deptId;

}
