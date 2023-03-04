package com.en_circle.el.nodes;

import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElPair;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class ElEvalNode extends ElNode {

    private final Object value;
    private final ElEnvironment environment;
    private ElNode compiledNode;

    public ElEvalNode(ElNodeMetaInfo metaInfo, ElEnvironment environment, Object value) {
        super(metaInfo);
        this.value = value;
        this.environment = environment;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        if (compiledNode == null) {
            if (value instanceof ElPair pair) {
                compiledNode = new ElEvalListNode(getMetaInfo(), pair, environment);
            } else {
                compiledNode = new ElLiteralNode(getMetaInfo(), value);
            }
        }
        return compiledNode.executeGeneric(frame);
    }
}
