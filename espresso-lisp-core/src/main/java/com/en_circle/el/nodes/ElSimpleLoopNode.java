package com.en_circle.el.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

public class ElSimpleLoopNode extends ElRepeatingNode {


    public ElSimpleLoopNode(ElNodeMetaInfo metaInfo) {
        super(metaInfo);
    }

    @Override
    public boolean executeRepeating(VirtualFrame frame) {
        return false;
    }
}
