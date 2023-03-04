package com.en_circle.el.nodes;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import com.oracle.truffle.api.source.SourceSection;

public abstract class ElRepeatingNode  extends Node implements RepeatingNode {

    protected final ElNodeMetaInfo metaInfo;

    public ElRepeatingNode(ElNodeMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
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
