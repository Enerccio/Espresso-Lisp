package com.en_circle.el.nodes;


import com.en_circle.el.context.ElContext;
import com.en_circle.el.context.ElShapeFactory;
import com.en_circle.el.nodes.control.ElRerunEvalException;
import com.en_circle.el.runtime.ElClosure;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import java.util.function.Supplier;

public class ElOpenClosureNode extends ElNode implements ElReplacingNode {

    private ElNode block;
    private final Supplier<ElClosure> closureContainer;

    public ElOpenClosureNode(ElNodeMetaInfo metaInfo, ElNode block, Supplier<ElClosure> closureContainer) {
        super(metaInfo);
        this.block = block;
        this.closureContainer = closureContainer;
    }

    @Override
    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        setClosure(frame);
        for (;;) {
            try {
                return block.executeInt(frame);
            } catch (ElRerunEvalException ignored) {

            }
        }
    }

    @Override
    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        setClosure(frame);
        for (;;) {
            try {
                return block.executeDouble(frame);
            } catch (ElRerunEvalException ignored) {

            }
        }
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        setClosure(frame);
        for (;;) {
            try {
                return block.executeGeneric(frame);
            } catch (ElRerunEvalException ignored) {

            }
        }
    }

    private void setClosure(VirtualFrame frame) {
        ElClosure parentClosure = closureContainer == null ? null : closureContainer.get();
        ElClosure closure = ElShapeFactory.allocateClosure(ElContext.get(this), parentClosure);
        frame.setObject(SLOT_CLOSURE, closure);
    }

    @Override
    public void replace(ElNode node) {
        block = node;
        throw new ElRerunEvalException();
    }
}
