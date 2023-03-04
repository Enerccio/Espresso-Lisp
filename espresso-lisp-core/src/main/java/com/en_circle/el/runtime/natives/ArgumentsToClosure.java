package com.en_circle.el.runtime.natives;

import com.en_circle.el.runtime.ElClosure;

public interface ArgumentsToClosure {

    void setSymbolBindings(ElClosure closure, Object[] arguments);

}
