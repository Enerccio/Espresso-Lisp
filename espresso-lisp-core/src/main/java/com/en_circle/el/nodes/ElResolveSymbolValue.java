package com.en_circle.el.nodes;

import com.en_circle.el.context.ElContext;
import com.en_circle.el.context.exceptions.ElBindException;
import com.en_circle.el.context.exceptions.ElVMStateException;
import com.en_circle.el.runtime.ElClosure;
import com.en_circle.el.runtime.ElSymbol;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class ElResolveSymbolValue extends ElNode {

    private final ElSymbol symbol;

    public ElResolveSymbolValue(ElNodeMetaInfo metaInfo, ElSymbol symbol) {
        super(metaInfo);
        this.symbol = symbol;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        if (!(frame.getValue(SLOT_CLOSURE) instanceof ElClosure closure)) {
            throw new ElVMStateException("Frame closure unbound");
        }

        return resolveClosureBinding(closure, symbol);
    }

    private Object resolveClosureBinding(ElClosure closure, ElSymbol symbol) {
        ElSymbol nil = ElContext.get(this).getNil();
        if (nil == symbol)
            return nil;
        ElSymbol t = ElContext.get(this).getT();
        if (t == symbol)
            return t;

        ElClosure currentClosure = closure;
        while (currentClosure != null) {
            if (closure.hasBinding(symbol)) {
                return closure.getBinding(symbol);
            }
            currentClosure = currentClosure.getParentClosure();
        }
        throw new ElBindException(metaInfo.getCallPlace(), closure, symbol, this);
    }
}
