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
 * @since 2019-07-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SchedulerDeptInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String name;
    /**
     * 部门邮箱
     */
    private String mail;
    /**
     * 部门执行器执行线程数
     */
    private Integer threadPoolNum;

    private String description;

    private String creator;

    private Long createTime;

    private Long updateTime;


}
