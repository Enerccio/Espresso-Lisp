package com.en_circle.el.context.exceptions;

import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.nodes.Node;

public class ElException extends AbstractTruffleException {

    public ElException() {
    }

    public ElException(Node location) {
        super(location);
    }

    public ElException(String message) {
        super(message);
    }

    public ElException(String message, Node location) {
        super(message, location);
    }

    public ElException(AbstractTruffleException prototype) {
        super(prototype);
    }

    public ElException(String message, Throwable cause, int stackTraceElementLimit, Node location) {
        super(message, cause, stackTraceElementLimit, location);
    }
}
