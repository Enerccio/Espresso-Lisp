package com.en_circle.el.runtime.natives;

import com.en_circle.el.context.ElSymbolHelper;
import com.en_circle.el.runtime.ElSymbol;

public class NativeArgument {

    public static NativeArgument withName(String name) {
        return new NativeArgument(name);
    }

    private final String name;
    private ElSymbol symbol;

    private NativeArgument(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ElSymbol getSymbol() {
        if (symbol == null) {
            symbol = ElSymbolHelper.getSymbol(getName());
        }
        return symbol;
    }
}
