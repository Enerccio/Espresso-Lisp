package com.en_circle.el.context.functions;

import com.en_circle.el.context.ElContext;
import com.en_circle.el.context.exceptions.ElRuntimeException;
import com.en_circle.el.runtime.ElClosure;
import com.en_circle.el.runtime.ElNativeFunction;
import com.en_circle.el.runtime.ElPair;
import com.en_circle.el.runtime.natives.InvokeWithArguments;
import com.en_circle.el.runtime.natives.NativeArgument;
import com.en_circle.el.runtime.natives.NativeStaticMethodCompiler;
import com.oracle.truffle.api.nodes.Node;

public class LispMinus implements InvokeWithArguments {
    public static final String NAME = "-";
    public static final String SOURCE_ARGUMENT = "args";

    private final ElContext context;

    public LispMinus(ElContext context) {
        this.context = context;
    }

    public ElNativeFunction build() {
        return NativeStaticMethodCompiler.compileFunction(context.getEnvironment(),
                NAME, this, NativeArgument.withName(SOURCE_ARGUMENT));
    }

    @Override
    public Object invoke(ElClosure closure, Object self, Node callTarget) throws Exception {
        Object args = getFromClosure(closure, SOURCE_ARGUMENT, callTarget);
        boolean hasDouble = false;
        for (Object o : ElPair.asIterator(args)) {
            if (o instanceof Number) {
                hasDouble |= (o instanceof Double);
            } else {
                throw new ElRuntimeException("- requires numeric arguments");
            }
        }
        if (hasDouble) {
            Double d = null;
            for (Object o : ElPair.asIterator(args)) {
                if (d == null) {
                    d = ((Number) o).doubleValue();
                } else {
                    d = d - ((Number) o).doubleValue();
                }
            }
            if (d == null) {
                d = 0.0;
            }
            return d;
        } else {
            Integer i = null;
            for (Object o : ElPair.asIterator(args)) {
                if (i == null) {
                    i = ((Number) o).intValue();
                } else {
                    i = i - ((Number) o).intValue();
                }
            }
            if (i == null) {
                i = 0;
            }
            return i;
        }
    }

    @Override
    public String getNativeInvocationPlace() {
        return NAME;
    }
}