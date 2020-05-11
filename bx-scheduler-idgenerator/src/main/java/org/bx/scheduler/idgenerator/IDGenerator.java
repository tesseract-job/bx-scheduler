package org.bx.scheduler.idgenerator;

import org.bx.scheduler.idgenerator.entity.IDGeneratorContext;

public interface IDGenerator {
    String generate(IDGeneratorContext context);
}
