package com.en_circle.el.context.exceptions;

import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElSymbol;
import com.oracle.truffle.api.nodes.Node;

public class ElEnvironmentBindException extends ElException {

    private final ElEnvironment environment;
    private final ElSymbol offendingSymbol;

    public ElEnvironmentBindException(String bindLocation, ElEnvironment environment, ElSymbol offendingSymbol, Node callNode) {
        super(String.format("Callable symbol %s unbound in environment %s <location %s, line %s>",
                        offendingSymbol.toDisplayString(true), environment, bindLocation,
                        callNode != null && callNode.getEncapsulatingSourceSection() != null ?
                                callNode.getEncapsulatingSourceSection().getStartLine() : -1),
                callNode);
        this.environment = environment;
        this.offendingSymbol = offendingSymbol;
    }

    public ElEnvironment getEnvironment() {
        return environment;
    }

    public ElSymbol getOffendingSymbol() {
        return offendingSymbol;
    }
}
