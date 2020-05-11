package org.bx.scheduler.lock.entity;

import lombok.Data;

@Data
public class SchedulerLockInfo {
    private String lockName;
    private String identity;
}
