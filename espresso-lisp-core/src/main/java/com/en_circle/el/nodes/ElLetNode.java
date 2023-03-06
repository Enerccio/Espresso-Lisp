package com.en_circle.el.nodes;

import com.en_circle.el.context.ElContext;
import com.en_circle.el.context.ElShapeFactory;
import com.en_circle.el.context.TailCall;
import com.en_circle.el.context.TailCallGuard;
import com.en_circle.el.context.exceptions.ElCompileException;
import com.en_circle.el.context.exceptions.ElVMStateException;
import com.en_circle.el.runtime.ElClosure;
import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElPair;
import com.en_circle.el.runtime.ElSymbol;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ElLetNode extends ElNode {

    private final ElBlockNode block;
    private final List<ElEvalNode> value = new ArrayList<>();
    private final List<ElSymbol> place = new ArrayList<>();
    private final int bindCount;

    public ElLetNode(ElNodeMetaInfo metaInfo, ElEnvironment environment, Object arguments) {
        super(metaInfo);
        if (!(arguments instanceof ElPair pair)) {
            throw new ElCompileException("let with incorrect number of arguments", this);
        }

        Object bindings = ElPair.car(pair);
        if (!(bindings instanceof ElPair) && bindings != ElContext.get(this).getNil()) {
            throw new ElCompileException("let with incorrect binding", this);
        }

        for (Object binding : ElPair.asIterator(bindings)) {
            if (!(binding instanceof ElPair bpair)) {
                throw new ElCompileException("let with incorrect binding", this);
            }
            Object bindPlace = ElPair.car(bpair);
            if (!(bindPlace instanceof ElSymbol symbol)) {
                throw new ElCompileException("let with incorrect binding", this);
            }
            Object bindEvaluation = ElPair.nth(bpair, 1);

            place.add(symbol);
            value.add(new ElEvalNode(metaInfo,environment, bindEvaluation));
        }
        Object code = ElPair.cdr(pair);

        block = new ElBlockNode(metaInfo, environment, code);
        bindCount = value.size();
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        if (!(frame.getValue(SLOT_CLOSURE) instanceof ElClosure closureOld)) {
            throw new ElVMStateException("Frame closure unbound");
        }

        ElClosure closure = ElShapeFactory.allocateClosure(ElContext.get(this), closureOld);
        frame.setObject(SLOT_CLOSURE, closure);

        Map<ElSymbol, Object> values = new LinkedHashMap<>();
        for (int ix=0; ix<bindCount; ix++) {
            try (TailCallGuard ignored = new TailCallGuard(TailCall.NO)) {
                values.put(place.get(ix), value.get(ix).executeGeneric(frame));
            }
        }

        for (ElSymbol symbol : values.keySet())
            closure.setBinding(symbol, values.get(symbol));

        return block.executeGeneric(frame);
    }
}
