package com.en_circle.el.nodes;

import com.en_circle.el.context.exceptions.ElCompileException;
import com.en_circle.el.context.exceptions.ElVMStateException;
import com.en_circle.el.runtime.ElClosure;
import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElPair;
import com.en_circle.el.runtime.ElSymbol;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class ElLetSingleNode extends ElNode {

    private final ElBlockNode block;
    private final ElEvalNode value;
    private final ElSymbol place;

    public ElLetSingleNode(ElNodeMetaInfo metaInfo, ElEnvironment environment, Object arguments) {
        super(metaInfo);
        if (!(arguments instanceof ElPair pair)) {
            throw new ElCompileException("let! with incorrect number of arguments", this);
        }
        Object binding = ElPair.car(pair);
        if (!(binding instanceof ElPair bpair)) {
            throw new ElCompileException("let! with incorrect binding", this);
        }
        Object bindPlace = ElPair.car(bpair);
        if (!(bindPlace instanceof ElSymbol symbol)) {
            throw new ElCompileException("let! with incorrect binding", this);
        }
        place = symbol;
        Object bindEvaluation = ElPair.nth(bpair, 1);
        Object code = ElPair.cdr(pair);

        value = new ElEvalNode(metaInfo,environment,bindEvaluation);
        block = new ElBlockNode(metaInfo, environment, code);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        if (!(frame.getValue(SLOT_CLOSURE) instanceof ElClosure closure)) {
            throw new ElVMStateException("Frame closure unbound");
        }
        closure.setBinding(place, value.executeGeneric(frame));
        return block.executeGeneric(frame);
    }
}
