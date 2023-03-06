package com.en_circle.el.nodes;

import com.en_circle.el.context.QuoteContext;
import com.en_circle.el.runtime.ElEnvironment;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class ElBackQuoteNode extends ElNode {

    private final ElNode evalNode;

    public ElBackQuoteNode(ElNodeMetaInfo metaInfo, ElEnvironment environment, Object arguments) {
        super(metaInfo);
        evalNode = new ElBackQuoteEvalNode(metaInfo, environment, arguments);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        return QuoteContext.inBackquote(evalNode, frame);
    }

}
