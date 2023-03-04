package com.en_circle.el.context.exceptions;

import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.nodes.Node;

public class ElVMStateException extends ElRuntimeException {

    public ElVMStateException() {
    }

    public ElVMStateException(Node location) {
        super(location);
    }

    public ElVMStateException(String message) {
        super(message);
    }

    public ElVMStateException(String message, Node location) {
        super(message, location);
    }

    public ElVMStateException(AbstractTruffleException prototype) {
        super(prototype);
    }

    public ElVMStateException(String message, Throwable cause, int stackTraceElementLimit, Node location) {
        super(message, cause, stackTraceElementLimit, location);
    }

    public ElVMStateException(Throwable t) {
        super(t);
    }

}
