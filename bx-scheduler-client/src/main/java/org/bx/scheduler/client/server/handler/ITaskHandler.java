package org.bx.scheduler.client.server.handler;

import org.bx.scheduler.client.server.HandleContext;

public interface ITaskHandler {
    void handle(HandleContext context);
}
