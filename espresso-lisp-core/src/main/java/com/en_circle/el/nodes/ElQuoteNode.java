package com.en_circle.el.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class ElQuoteNode extends ElNode {

    private final Object arguments;

    public ElQuoteNode(ElNodeMetaInfo metaInfo, Object arguments) {
        super(metaInfo);
        this.arguments = arguments;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        return arguments;
    }
}
