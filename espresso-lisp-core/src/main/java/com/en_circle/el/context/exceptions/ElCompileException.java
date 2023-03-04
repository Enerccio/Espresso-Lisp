package com.en_circle.el.context.exceptions;

import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.nodes.Node;

public class ElCompileException extends ElException {
    public ElCompileException() {
    }

    public ElCompileException(Node location) {
        super(location);
    }

    public ElCompileException(String message) {
        super(message);
    }

    public ElCompileException(String message, Node location) {
        super(message, location);
    }

    public ElCompileException(AbstractTruffleException prototype) {
        super(prototype);
    }

    public ElCompileException(String message, Throwable cause, int stackTraceElementLimit, Node location) {
        super(message, cause, stackTraceElementLimit, location);
    }
}
