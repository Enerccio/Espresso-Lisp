package com.en_circle.el.context;

import com.en_circle.el.runtime.ElSymbol;
import com.oracle.truffle.api.nodes.Node;
import org.apache.commons.lang3.StringUtils;

public class ElSymbolHelper {

    public static ElSymbol getSymbol(String symbolName) {
        ElContext context = ElContext.get(null);
        return context.allocateSymbol(symbolName);
    }

    public static ElSymbol[] splitOnFirstCompound(Node node, ElSymbol symbol) {
        ElContext context = ElContext.get(node);
        String name = symbol.getName();
        int ix = name.indexOf(':');
        ElSymbol firstSymbol = context.allocateSymbol(name.substring(0, ix));
        ElSymbol secondSymbol = context.allocateSymbol(name.substring(ix+1));
        return new ElSymbol[] { firstSymbol, secondSymbol };
    }

}
