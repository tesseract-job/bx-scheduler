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

public class SchedulerExecutorInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String creator;
    private String deptName;
    private String deptId;
    private String description;
    private Long createTime;
}
