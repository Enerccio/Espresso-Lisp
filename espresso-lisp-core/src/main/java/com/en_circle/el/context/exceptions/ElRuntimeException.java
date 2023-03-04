package com.en_circle.el.context.exceptions;

import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.nodes.Node;

public class ElRuntimeException extends ElException {
    public ElRuntimeException() {
    }

    public ElRuntimeException(Node location) {
        super(location);
    }

    public ElRuntimeException(String message) {
        super(message);
    }

    public ElRuntimeException(String message, Node location) {
        super(message, location);
    }

    public ElRuntimeException(AbstractTruffleException prototype) {
        super(prototype);
    }

    public ElRuntimeException(String message, Throwable cause, int stackTraceElementLimit, Node location) {
        super(message, cause, stackTraceElementLimit, location);
    }

    public ElRuntimeException(Throwable t) {
        super(t.getMessage(), t, AbstractTruffleException.UNLIMITED_STACK_TRACE, null);
    }
}
