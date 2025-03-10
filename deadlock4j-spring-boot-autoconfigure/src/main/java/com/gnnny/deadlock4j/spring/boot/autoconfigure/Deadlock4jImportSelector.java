package com.gnnny.deadlock4j.spring.boot.autoconfigure;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class Deadlock4jImportSelector implements DeferredImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        if (importingClassMetadata.hasAnnotation(EnableDeadlock4j.class.getName())) {
            return new String[]{
                "com.gnnny.deadlock4j.spring.boot.autoconfigure.DeadlockBusterAutoConfiguration"
            };
        }

        return new String[0];
    }
}
