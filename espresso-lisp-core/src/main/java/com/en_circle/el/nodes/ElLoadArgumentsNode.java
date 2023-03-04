package com.en_circle.el.nodes;

import com.en_circle.el.context.exceptions.ElVMStateException;
import com.en_circle.el.runtime.ElClosure;
import com.en_circle.el.runtime.natives.ArgumentsToClosure;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class ElLoadArgumentsNode extends ElNode {

    private final ElNode block;
    private final ArgumentsToClosure argumentsToClosure;

    public ElLoadArgumentsNode(ElNodeMetaInfo metaInfo, ElNode block, ArgumentsToClosure argumentsToClosure) {
        super(metaInfo);
        this.block = block;
        this.argumentsToClosure = argumentsToClosure;
    }

    @Override
    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        loadArguments(frame);
        return block.executeInt(frame);
    }

    @Override
    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        loadArguments(frame);
        return block.executeDouble(frame);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        loadArguments(frame);
        return block.executeGeneric(frame);
    }

    private void loadArguments(VirtualFrame frame) {
        if (!(frame.getValue(SLOT_CLOSURE) instanceof ElClosure closure)) {
            throw new ElVMStateException("Frame closure unbound");
        }

        argumentsToClosure.setSymbolBindings(closure, frame.getArguments());
    }
}
