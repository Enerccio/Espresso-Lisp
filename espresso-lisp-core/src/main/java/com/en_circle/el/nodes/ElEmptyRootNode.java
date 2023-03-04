package com.en_circle.el.nodes;

import com.en_circle.el.ElLanguage;
import com.en_circle.el.context.ElContext;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

public class ElEmptyRootNode extends RootNode {

    public ElEmptyRootNode() {
        super(ElLanguage.get(null));
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return ElContext.get(this).getNil();
    }

}
