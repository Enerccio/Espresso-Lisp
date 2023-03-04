package com.en_circle.el.context.exceptions;

import com.oracle.truffle.api.exception.AbstractTruffleException;

public class ElInvocationTargetException extends ElRuntimeException {

    public ElInvocationTargetException(Throwable cause) {
        super(cause.getMessage(), cause, AbstractTruffleException.UNLIMITED_STACK_TRACE, null);
    }

}
