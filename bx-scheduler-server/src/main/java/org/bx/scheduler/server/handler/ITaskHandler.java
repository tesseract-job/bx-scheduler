package org.bx.scheduler.server.handler;

import org.bx.scheduler.server.HandleContext;

public interface ITaskHandler {
    void handle(HandleContext context);
}
