package com.en_circle.el.context.functions;

import com.en_circle.el.context.ElContext;
import com.en_circle.el.runtime.ElNativeFunction;
import com.en_circle.el.runtime.ElPair;
import com.en_circle.el.runtime.natives.InvokeTwoArguments;
import com.en_circle.el.runtime.natives.NativeArgument;
import com.en_circle.el.runtime.natives.NativeStaticMethodCompiler;

public class LispCons implements InvokeTwoArguments {
    public static final String NAME = "cons";

    private final ElContext context;

    public LispCons(ElContext context) {
        this.context = context;
    }

    public ElNativeFunction build() {
        return NativeStaticMethodCompiler.compileTwoArguments(context.getEnvironment(),
                NAME, this, NativeArgument.withName("a"), NativeArgument.withName("b"));
    }

    @Override
    public Object invoke(Object a, Object b) throws Exception {
        return new ElPair(a, b);
    }
}