package com.en_circle.el.context;

import com.en_circle.el.runtime.ElClosure;
import com.en_circle.el.runtime.ElEnvironment;
import com.oracle.truffle.api.instrumentation.AllocationReporter;
import com.oracle.truffle.api.object.Shape;

public class ElShapeFactory {

    public static final Shape ENVIRONMENT_SHAPE = Shape.newBuilder().layout(ElEnvironment.class).build();

    public static ElEnvironment allocateEnvironment(ElContext context) {
        AllocationReporter reporter = context.getReporter();
        reporter.onEnter(null, 0, AllocationReporter.SIZE_UNKNOWN);
        ElEnvironment object = new ElEnvironment(ENVIRONMENT_SHAPE);
        reporter.onReturnValue(object, 0, AllocationReporter.SIZE_UNKNOWN);
        return object;
    }

    public static final Shape CLOSURE_SHAPE = Shape.newBuilder().layout(ElClosure.class).build();

    public static ElClosure allocateClosure(ElContext context, ElClosure parentClosure) {
        AllocationReporter reporter = context.getReporter();
        reporter.onEnter(null, 0, AllocationReporter.SIZE_UNKNOWN);
        ElClosure object = new ElClosure(CLOSURE_SHAPE, parentClosure);
        reporter.onReturnValue(object, 0, AllocationReporter.SIZE_UNKNOWN);
        return object;
    }

}
