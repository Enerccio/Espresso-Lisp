package com.en_circle.el.context.exceptions;

import com.en_circle.el.runtime.ElClosure;
import com.en_circle.el.runtime.ElSymbol;
import com.oracle.truffle.api.nodes.Node;

public class ElBindException extends ElException {

    private final ElClosure closure;
    private final ElSymbol offendingSymbol;

    public ElBindException(String bindLocation, ElClosure closure, ElSymbol offendingSymbol, Node callTarget) {
        super(String.format("Symbol %s unbound in %s", offendingSymbol, bindLocation), callTarget);
        this.closure = closure;
        this.offendingSymbol = offendingSymbol;
    }

    public ElClosure getClosure() {
        return closure;
    }

    public ElSymbol getOffendingSymbol() {
        return offendingSymbol;
    }
}
