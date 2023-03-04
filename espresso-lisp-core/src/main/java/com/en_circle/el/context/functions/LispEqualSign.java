package com.en_circle.el.context.functions;

import com.en_circle.el.context.ElContext;
import com.en_circle.el.runtime.ElClosure;
import com.en_circle.el.runtime.ElNativeFunction;
import com.en_circle.el.runtime.ElPair;
import com.en_circle.el.runtime.natives.InvokeWithArguments;
import com.en_circle.el.runtime.natives.NativeArgument;
import com.en_circle.el.runtime.natives.NativeStaticMethodCompiler;
import com.oracle.truffle.api.nodes.Node;

import java.util.Objects;

public class LispEqualSign implements InvokeWithArguments {
    public static final String NAME = "=";
    public static final String SOURCE_ARGUMENT = "args";

    private final ElContext context;

    public LispEqualSign(ElContext context) {
        this.context = context;
    }

    public ElNativeFunction build() {
        return NativeStaticMethodCompiler.compileFunction(context.getEnvironment(),
                NAME, this, NativeArgument.withName(SOURCE_ARGUMENT));
    }

    @Override
    public Object invoke(ElClosure closure, Object self, Node callTarget) throws Exception {
        Object args = getFromClosure(closure, SOURCE_ARGUMENT, callTarget);
        boolean result = true;
        Object test = null;
        for (Object o : ElPair.asIterator(args)) {
            if (test == null) {
                test = o;
            } else {
                if (!Objects.equals(test, o)) {
                    result = false;
                    break;
                }
            }
        }
        if (result) {
            return ElContext.get(null).getT();
        } else {
            return ElContext.get(null).getNil();
        }
    }

    @Override
    public String getNativeInvocationPlace() {
        return NAME;
    }
}