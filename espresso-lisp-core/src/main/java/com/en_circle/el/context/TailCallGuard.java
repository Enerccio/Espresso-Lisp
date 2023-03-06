package com.en_circle.el.context;

import com.en_circle.el.runtime.ElFunction;

public class TailCallGuard implements AutoCloseable {

    public static final ThreadLocal<TailCall> STATE = ThreadLocal.withInitial(() -> TailCall.YES);
    public static final ThreadLocal<ElFunction> CURRENT_FUNCTION = new ThreadLocal<>();

    private final TailCall current;
    private final ElFunction call;

    public TailCallGuard(TailCall newState) {
        this(newState, CURRENT_FUNCTION.get());
    }

    public TailCallGuard(TailCall newState, ElFunction callFunction) {
        this.call = callFunction;
        this.current = STATE.get();
        STATE.set(newState);
        CURRENT_FUNCTION.set(callFunction);
    }

    @Override
    public void close() {
        STATE.set(current);
        CURRENT_FUNCTION.set(call);
    }

}
