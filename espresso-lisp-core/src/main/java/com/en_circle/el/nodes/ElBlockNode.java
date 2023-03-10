package com.en_circle.el.nodes;

import com.en_circle.el.context.ElContext;
import com.en_circle.el.context.TailCall;
import com.en_circle.el.context.TailCallGuard;
import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElHasSourceInfo;
import com.en_circle.el.runtime.ElPair;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import java.util.ArrayList;
import java.util.List;

public class ElBlockNode extends ElNode {

    private final List<ElNode> nodes = new ArrayList<>();

    public ElBlockNode(ElNodeMetaInfo metaInfo, ElEnvironment environment, Object expressions) {
        super(metaInfo);
        for (Object expression : ElPair.asIterator(expressions)) {
            nodes.add(new ElEvalNode(ElHasSourceInfo.get(expression), environment, expression));
        }
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        Object last = ElContext.get(this).getNil();
        for (int ix=0; ix<nodes.size(); ix++) {
            ElNode node = nodes.get(ix);
            try (TailCallGuard ignored = new TailCallGuard(ix == nodes.size() - 1 ? TailCall.YES : TailCall.NO)) {
                last = node.executeGeneric(frame);
            }
        }
        return last;
    }
}
