package com.en_circle.el.nodes;

import com.en_circle.el.ElLanguage;
import com.en_circle.el.context.exceptions.ElRuntimeException;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;

public class ElStartEvalChainNode extends RootNode {

    public static ElStartEvalChainNode create(ElNodeMetaInfo metaInfo, ElNode node) {
        FrameDescriptor.Builder builder = FrameDescriptor.newBuilder(2);
        builder.addSlot(FrameSlotKind.Object, ElNode.SLOT_CLOSURE, null);
        builder.addSlot(FrameSlotKind.Object, ElNode.SLOT_THIS, null);
        return new ElStartEvalChainNode(node, metaInfo, builder.build());
    }

    private final ElNode node;
    private final ElNodeMetaInfo metaInfo;

    private ElStartEvalChainNode(ElNode node, ElNodeMetaInfo metaInfo, FrameDescriptor frameDescriptor) {
        super(ElLanguage.get(null), frameDescriptor);
        this.metaInfo = metaInfo;
        this.node = node;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        try {
            return node.executeGeneric(frame);
        } catch (UnexpectedResultException e) {
            throw new ElRuntimeException(e);
        }
    }

    @Override
    public String getQualifiedName() {
        return metaInfo.getCallPlace();
    }

    @Override
    public String getName() {
        return metaInfo.getCallPlace();
    }

    @Override
    public SourceSection getSourceSection() {
        return metaInfo.getSourceSection();
    }

    @Override
    public boolean isInternal() {
        return false;
    }
}
