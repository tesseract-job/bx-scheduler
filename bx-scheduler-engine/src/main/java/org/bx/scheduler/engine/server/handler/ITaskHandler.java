package org.bx.scheduler.engine.server.handler;

import org.bx.scheduler.engine.server.HandleContext;

public interface ITaskHandler {
    void handle(HandleContext context);
}
