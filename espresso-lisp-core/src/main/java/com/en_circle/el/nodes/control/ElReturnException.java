package com.en_circle.el.nodes.control;

import com.oracle.truffle.api.nodes.ControlFlowException;

public class ElReturnException extends ControlFlowException {

    private final Object returnValue;

    public ElReturnException(Object returnValue) {
        this.returnValue = returnValue;
    }

    public Object getReturnValue() {
        return returnValue;
    }
}
