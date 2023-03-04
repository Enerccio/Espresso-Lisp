package com.en_circle.el.nodes;

import com.en_circle.el.nodes.types.ElNumberTypeSystem;
import com.en_circle.el.nodes.types.ElNumberTypeSystemGen;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;
import org.graalvm.polyglot.Source;

@TypeSystemReference(ElNumberTypeSystem.class)
public abstract class ElNode extends Node {

    public static final int SLOT_CLOSURE = 0;
    public static final int SLOT_THIS = 1;

    protected final ElNodeMetaInfo metaInfo;

    public ElNode(ElNodeMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }

    public int executeInt(VirtualFrame frame) throws UnexpectedResultException {
        return ElNumberTypeSystemGen.expectInteger(this.executeGeneric(frame));
    }

    public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
        return ElNumberTypeSystemGen.expectDouble(this.executeGeneric(frame));
    }

    public abstract Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException;

    public ElNodeMetaInfo getMetaInfoCopy() {
        return metaInfo.copy();
    }

    public ElNodeMetaInfo getMetaInfo() {
        return metaInfo;
    }

    @Override
    public SourceSection getSourceSection() {
        if (metaInfo == null)
            return null;
        return metaInfo.getSourceSection();
    }

}
