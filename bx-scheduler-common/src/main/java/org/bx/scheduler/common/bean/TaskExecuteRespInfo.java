package org.bx.scheduler.common.bean;

import lombok.Data;

@Data
public class TaskExecuteRespInfo {
    private String futureId;
    private int state;
    private String exception;
}
