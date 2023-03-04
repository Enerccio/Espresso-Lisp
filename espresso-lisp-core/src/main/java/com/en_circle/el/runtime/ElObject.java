package com.en_circle.el.runtime;

import com.oracle.truffle.api.interop.TruffleObject;

public interface ElObject extends TruffleObject {

    Object toDisplayString(boolean allowSideEffects);

}
