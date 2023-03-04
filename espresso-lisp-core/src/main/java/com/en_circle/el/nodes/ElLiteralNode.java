package com.en_circle.el.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class ElLiteralNode extends ElNode {

    private final Object literal;

    public ElLiteralNode(ElNodeMetaInfo metaInfo, Object literal) {
        super(metaInfo);

        this.literal = literal;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        return literal;
    }
}
