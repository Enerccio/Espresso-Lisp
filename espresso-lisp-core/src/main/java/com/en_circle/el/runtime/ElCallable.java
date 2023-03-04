package com.en_circle.el.runtime;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

@ExportLibrary(InteropLibrary.class)
public abstract class ElCallable implements ElObject {

    protected CallTarget callTarget;
    protected ElClosure closure;

    public ElCallable(ElClosure closure) {
        this.closure = closure;
    }

    public void setCallTarget(CallTarget callTarget) {
        this.callTarget = callTarget;
    }

    public CallTarget getCallTarget() {
        return callTarget;
    }

    public ElClosure getClosure() {
        return closure;
    }

    @ExportMessage
    boolean isExecutable() {
        return true;
    }

    @ExportMessage
    Object execute(Object[] arguments) {
        return callTarget.call(arguments);
    }

    public boolean isMacro() {
        return false;
    }

}
