package com.en_circle.el.context;

import com.en_circle.el.runtime.ElClosure;

public interface ArgumentsToClosure {

    void setSymbolBindings(ElClosure closure, Object[] arguments);

}
