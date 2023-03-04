package com.en_circle.el.nodes;

import com.en_circle.el.context.ElSymbolHelper;
import com.en_circle.el.context.exceptions.ElEnvironmentBindException;
import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElSymbol;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class ElResolveFunctionValueNode extends ElNode {

    private final ElEnvironment environment;
    private final ElSymbol symbol;

    public ElResolveFunctionValueNode(ElNodeMetaInfo metaInfo, ElSymbol symbol, ElEnvironment environment) {
        super(metaInfo);
        this.environment = environment;
        this.symbol = symbol;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        return resolveEnvironmentBinding(symbol);
    }

    private Object resolveEnvironmentBinding(ElSymbol symbol) {
        ElSymbol currentSymbol = symbol;
        ElEnvironment currentEnvironment = environment;
        while (currentEnvironment != null && currentSymbol != null) {
            if (currentSymbol.isCompound()) {
                ElSymbol[] parts = ElSymbolHelper.splitOnFirstCompound(this, symbol);
                if (!currentEnvironment.hasBinding(parts[1]))
                    break;
                Object value = currentEnvironment.getBinding(parts[1]);
                if (!(value instanceof ElEnvironment subEnvironment))
                    break;
                currentEnvironment = subEnvironment;
                currentSymbol = parts[1];
            } else {
                if (!currentEnvironment.hasBinding(currentSymbol)) {
                    break;
                }
                return currentEnvironment.getBinding(currentSymbol);
            }
        }
        throw new ElEnvironmentBindException(metaInfo.getCallPlace(), environment, currentSymbol, this);
    }


}
