package org.bx.scheduler.common.bean;

import lombok.Data;

@Data
public class TaskExecuteInfo {
    private int shardingIndex;
    private String className;
    private String args;
    private String logId;
    private String firedId;
}
