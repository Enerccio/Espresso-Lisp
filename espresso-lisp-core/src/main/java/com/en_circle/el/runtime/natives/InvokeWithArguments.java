package com.en_circle.el.runtime.natives;

import com.en_circle.el.context.ElSymbolHelper;
import com.en_circle.el.context.exceptions.ElBindException;
import com.en_circle.el.context.exceptions.ElRuntimeException;
import com.en_circle.el.runtime.ElClosure;
import com.en_circle.el.runtime.ElSymbol;
import com.oracle.truffle.api.nodes.Node;

public interface InvokeWithArguments {

    Object invoke(ElClosure closure, Object self, Node callTarget) throws Exception;

    String getNativeInvocationPlace();

    default <T> T getFromClosure(ElClosure closure, String name, Class<T> clazz, Node callTarget) {
        ElSymbol symbol = ElSymbolHelper.getSymbol(name);

        ElClosure currentClosure = closure;
        while (currentClosure != null) {
            if (closure.hasBinding(symbol)) {
                Object o = closure.getBinding(symbol);
                if (clazz.isAssignableFrom(o.getClass())) {
                    return (T) o;
                }
                throw new ElRuntimeException("unexpected native argument type");
            }
            currentClosure = currentClosure.getParentClosure();
        }
        throw new ElBindException(getNativeInvocationPlace(), closure, symbol, callTarget);
    }

    default <T> T getFromClosure(ElClosure closure, String name, Node callTarget) {
        ElSymbol symbol = ElSymbolHelper.getSymbol(name);

        ElClosure currentClosure = closure;
        while (currentClosure != null) {
            if (closure.hasBinding(symbol)) {
                Object o = closure.getBinding(symbol);
                return (T) o;
            }
            currentClosure = currentClosure.getParentClosure();
        }
        throw new ElBindException(getNativeInvocationPlace(), closure, symbol, callTarget);
    }

}
