package com.en_circle.el.nodes;

import com.en_circle.el.context.exceptions.ElCompileException;
import com.en_circle.el.runtime.ElClosure;
import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElSymbol;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class ElSetNode extends ElNode {


    private final ElNode bindNode;
    private final ElNode evalNode;

    public ElSetNode(ElNodeMetaInfo metaInfo, ElEnvironment environment, Object binding, Object value) {
        super(metaInfo);
        this.bindNode = new ElEvalNode(metaInfo, environment, binding);
        this.evalNode = new ElEvalNode(metaInfo, environment, value);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        Object binding = bindNode.executeGeneric(frame);
        if (!(binding instanceof ElSymbol name)) {
            throw new ElCompileException("bind place for set must be a symbol!");
        }
        ElClosure closure = (ElClosure) frame.getObject(SLOT_CLOSURE);
        closure.setBinding(name, evalNode.executeGeneric(frame));
        return name;
    }
}
