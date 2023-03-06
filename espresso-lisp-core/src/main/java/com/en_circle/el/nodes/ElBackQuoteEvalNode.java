package com.en_circle.el.nodes;

import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElPair;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import java.util.ArrayList;
import java.util.List;

public class ElBackQuoteEvalNode extends ElNode {

    private final List<ElNode> compiledNodes = new ArrayList<>();

    public ElBackQuoteEvalNode(ElNodeMetaInfo metaInfo, ElEnvironment environment, Object value) {
        super(metaInfo);

        if (value instanceof ElPair pair) {
            for (Object o : ElPair.asIterator(pair)) {
                compiledNodes.add(new ElEvalNode(metaInfo, environment, o));
            }
        } else {
            compiledNodes.add(new ElLiteralNode(metaInfo, value));
        }
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        List<Object> returns = new ArrayList<>();
        for (ElNode node : compiledNodes) {
            if (node instanceof ElEvalNode evalNode) {
                Object rv = node.executeGeneric(frame);
                if (evalNode.isSplice()) {
                    for (Object o : ElPair.asStrictIterator(rv)) {
                        returns.add(o);
                    }
                } else {
                    returns.add(rv);
                }
            } else {
                returns.add(node.executeGeneric(frame));
            }
        }
        return ElPair.fromList(returns);
    }
}
