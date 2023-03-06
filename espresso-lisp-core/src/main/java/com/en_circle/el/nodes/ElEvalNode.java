package com.en_circle.el.nodes;

import com.en_circle.el.context.QuoteContext;
import com.en_circle.el.nodes.control.ElRerunEvalException;
import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElPair;
import com.en_circle.el.runtime.ElSymbol;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class ElEvalNode extends ElNode implements ElReplacingNode {

    private final Object value;
    private final ElEnvironment environment;
    private boolean compiled;
    private ElNode compiledNode;

    public ElEvalNode(ElNodeMetaInfo metaInfo, ElEnvironment environment, Object value) {
        super(metaInfo);
        this.environment = environment;
        this.value = value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        if (!compiled) {
            if (value instanceof ElPair pair) {
                compiledNode = new ElEvalListNode(metaInfo, pair, environment);
                ((ElEvalListNode) compiledNode).setMacroExpandNode(this);
            } else if (value instanceof ElSymbol symbol && !QuoteContext.isInBackQuote()) {
                compiledNode = new ElResolveSymbolValue(metaInfo, symbol);
            } else {
                compiledNode = new ElLiteralNode(metaInfo, value);
            }
            compiled = true;
        }

        for (;;) {
            try {
                return compiledNode.executeGeneric(frame);
            } catch (ElRerunEvalException ignored) {

            }
        }
    }

    @Override
    public void replace(ElNode node) {
        compiledNode = node;
        throw new ElRerunEvalException();
    }

    public boolean isSplice() {
        return compiledNode instanceof ElEvalListNode listNode && listNode.isSplice();
    }
}
