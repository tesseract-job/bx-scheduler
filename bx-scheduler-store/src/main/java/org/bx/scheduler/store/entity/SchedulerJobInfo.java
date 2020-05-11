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
public class SchedulerJobInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String triggerId;

    private String className;

    private Long createTime;

    private String creator;


}
