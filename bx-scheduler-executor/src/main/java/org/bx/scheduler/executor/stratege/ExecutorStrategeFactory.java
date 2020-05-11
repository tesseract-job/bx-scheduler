package org.bx.scheduler.executor.stratege;

import java.util.HashMap;
import java.util.Map;

public class ExecutorStrategeFactory {
    private static final Map<Integer, IExecutorStratege> STRATEGE_MAP = new HashMap<>(8);

    public static IExecutorStratege createStratege(int strategeId) {
        return STRATEGE_MAP.get(strategeId);
    }
}
