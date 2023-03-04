package com.en_circle.el.context.functions;

import com.en_circle.el.ElLanguage;
import com.en_circle.el.context.ElContext;
import com.en_circle.el.runtime.ElNativeFunction;
import com.en_circle.el.runtime.natives.NativeArgument;
import com.en_circle.el.runtime.natives.NativeStaticMethodCompiler;

import java.util.function.Supplier;

public class LispEnvironmentInfo implements Supplier<Object> {
    public static final String NAME = "lisp-environment-info";

    private final ElContext context;

    public LispEnvironmentInfo(ElContext context) {
        this.context = context;
    }

    public ElNativeFunction build() {
        return NativeStaticMethodCompiler.compileNoArgsFunction(context.getEnvironment(),
                NAME, this);
    }

    @Override
    public String get() {
        return ElLanguage.INFORMATION;
    }

}
