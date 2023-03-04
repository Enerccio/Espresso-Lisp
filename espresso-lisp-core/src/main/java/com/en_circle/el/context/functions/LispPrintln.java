package com.en_circle.el.context.functions;

import com.en_circle.el.context.ElContext;
import com.en_circle.el.runtime.ElNativeFunction;
import com.en_circle.el.runtime.natives.InvokeSingleArgument;
import com.en_circle.el.runtime.natives.NativeArgument;
import com.en_circle.el.runtime.natives.NativeStaticMethodCompiler;

public class LispPrintln implements InvokeSingleArgument {
    public static final String NAME = "println";

    private final ElContext context;

    public LispPrintln(ElContext context) {
        this.context = context;
    }

    public ElNativeFunction build() {
        return NativeStaticMethodCompiler.compileSingleArgument(context.getEnvironment(),
                NAME, this, NativeArgument.withName("object"));
    }

    @Override
    public Object invoke(Object argument) throws Exception {
        System.out.println(argument);
        return argument;
    }
}
