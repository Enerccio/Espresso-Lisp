package com.en_circle.el.nodes;

import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElPair;
import com.en_circle.el.runtime.ElSymbol;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class ElEvalNode extends ElNode {

    private final ElNode compiledNode;

    public ElEvalNode(ElNodeMetaInfo metaInfo, ElEnvironment environment, Object value) {
        super(metaInfo);

        if (value instanceof ElPair pair) {
            compiledNode = new ElEvalListNode(metaInfo, pair, environment);
        } else if (value instanceof ElSymbol symbol) {
            compiledNode = new ElResolveSymbolValue(metaInfo, symbol);
        } else {
            compiledNode = new ElLiteralNode(metaInfo, value);
        }
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        return compiledNode.executeGeneric(frame);
    }
}
