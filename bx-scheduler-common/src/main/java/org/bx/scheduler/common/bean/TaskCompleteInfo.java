package org.bx.scheduler.common.bean;

import lombok.Data;

@Data
public class TaskCompleteInfo {
    private TaskExecuteInfo executeInfo;
    private String exception;
    private double loadFactor;
    private String host;
    private int port;
}
