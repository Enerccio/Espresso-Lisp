package com.en_circle.el.context.functions;

import com.en_circle.el.context.ElContext;
import com.en_circle.el.runtime.ElClosure;
import com.en_circle.el.runtime.ElNativeFunction;
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
        return ElContext.get(null).getMathContext().minus(args);
    }

    @Override
    public String getNativeInvocationPlace() {
        return NAME;
    }
}