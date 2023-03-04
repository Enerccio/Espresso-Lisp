package com.en_circle.el.context.functions;

import com.en_circle.el.context.ElContext;
import com.en_circle.el.runtime.ElNativeFunction;
import com.en_circle.el.runtime.ElPair;
import com.en_circle.el.runtime.natives.InvokeSingleArgument;
import com.en_circle.el.runtime.natives.NativeArgument;
import com.en_circle.el.runtime.natives.NativeStaticMethodCompiler;

public class LispCdr implements InvokeSingleArgument {
    public static final String NAME = "cdr";

    private final ElContext context;

    public LispCdr(ElContext context) {
        this.context = context;
    }

    public ElNativeFunction build() {
        return NativeStaticMethodCompiler.compileSingleArgument(context.getEnvironment(),
                NAME, this, NativeArgument.withName("l"));
    }

    @Override
    public Object invoke(Object argument) throws Exception {
        return ElPair.cdr(argument);
    }
}