package com.en_circle.el.context;

import com.en_circle.el.nodes.ElNode;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class QuoteContext {

    private static ThreadLocal<Boolean> QUOTE_CONTEXT = ThreadLocal.withInitial(() -> false);

    public static boolean isInBackQuote() {
        return QUOTE_CONTEXT.get();
    }

    public static Object inBackquote(ElNode node, VirtualFrame frame) throws UnexpectedResultException {
        boolean wasBackquote = QUOTE_CONTEXT.get();
        QUOTE_CONTEXT.set(true);
        try {
            return node.executeGeneric(frame);
        } finally {
            QUOTE_CONTEXT.set(wasBackquote);
        }
    }

    public static Object evaluateBackQuote(ElNode node, VirtualFrame frame) throws UnexpectedResultException {
        boolean wasBackquote = QUOTE_CONTEXT.get();
        QUOTE_CONTEXT.set(false);
        try {
            return node.executeGeneric(frame);
        } finally {
            QUOTE_CONTEXT.set(wasBackquote);
        }
    }

}
