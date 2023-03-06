package com.en_circle.el.nodes.control;

import com.oracle.truffle.api.nodes.ControlFlowException;

public class ElTailCallException extends ControlFlowException {

    private final Object[] newArguments;

    public ElTailCallException(Object[] newArguments) {
        this.newArguments = newArguments;
    }

    public Object[] getNewArguments() {
        return newArguments;
    }
}
