package com.en_circle.el.nodes;

import com.en_circle.el.context.ElContext;
import com.en_circle.el.context.QuoteContext;
import com.en_circle.el.context.exceptions.ElRuntimeException;
import com.en_circle.el.runtime.ElClosure;
import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElPair;
import com.en_circle.el.runtime.ElSymbol;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class ElEvalListNode extends ElNode {

    private final ElPair nodes;
    private final ElEnvironment environment;
    private EvalExpressionState state = EvalExpressionState.NEW;
    private ElNode compiled;
    private ElEnvironmentChangeNode toplevelNode;
    private ElReplacingNode parentNode;
    private boolean isSplice;
    private boolean evaluateInBackquote = false;

    public ElEvalListNode(ElNodeMetaInfo metaInfo, ElPair nodes, ElEnvironment environment) {
        super(metaInfo);
        this.environment = environment;
        this.nodes = nodes;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        if (state == EvalExpressionState.NEW) {
            compile((ElClosure) frame.getObject(SLOT_CLOSURE));
            state = EvalExpressionState.COMPILED;
        }
        if (evaluateInBackquote) {
            return QuoteContext.evaluateBackQuote(compiled, frame);
        } else {
            return compiled.executeGeneric(frame);
        }
    }

    private void compile(ElClosure closure) {
        if (!ElPair.empty(nodes)) {
            Object head = ElPair.car(nodes);
            Object arguments = ElPair.cdr(nodes);

            if (head instanceof ElSymbol symbol) {
                if (QuoteContext.isInBackQuote()) {
                    if ("unquote".equals(symbol.getName())) {
                        compiled = new ElEvalNode(getMetaInfo(), environment, ElPair.nth(nodes, 1));
                        evaluateInBackquote = true;
                    } else if ("unquote-splice".equals(symbol.getName())) {
                        compiled = new ElEvalNode(getMetaInfo(), environment, ElPair.nth(nodes, 1));
                        isSplice = true;
                        evaluateInBackquote = true;
                    } else {
                        compiled = new ElBackQuoteEvalNode(getMetaInfo(), environment, arguments);
                    }
                    return;
                }

                if ("quote".equals(symbol.getName())) {
                    compiled = new ElQuoteNode(getMetaInfo(), ElPair.nth(nodes, 1));
                } else if ("backquote".equals(symbol.getName())) {
                    compiled = new ElBackQuoteNode(getMetaInfo(), environment, ElPair.nth(nodes, 1));
                } else if ("unquote".equals(symbol.getName())) {
                    throw new ElRuntimeException("Unquote not in quote");
                } else if ("unquote-splice".equals(symbol.getName())) {
                    throw new ElRuntimeException("Unquote not in quote");
                } else if ("if".equals(symbol.getName())) {
                    compiled = new ElIfNode(getMetaInfo(), environment, arguments);
                } else if ("let".equals(symbol.getName())) {
                    compiled = new ElLetNode(getMetaInfo(), environment, arguments);
                } else if ("set".equals(symbol.getName())) {
                    Object binding = ElPair.car(arguments);
                    Object newValue = ElPair.cdr(arguments);
                    compiled = new ElSetNode(getMetaInfo(), environment, binding, ElPair.car(newValue));
                } else if ("int:define!".equals(symbol.getName())) {
                    compiled = new ElLiteralNode(getMetaInfo(),
                            ElContext.get(this).defineFunction(getMetaInfo(), environment, closure, arguments));
                } else {
                    compiled = new ElGenericSexpressionEval(getMetaInfo(), environment, symbol, arguments);
                    ((ElGenericSexpressionEval) compiled).setReplacingNode(parentNode);
                }
            } else {
                throw new ElRuntimeException("Head is not a symbol!");
            }
        }
    }

    public void setParentNode(ElEnvironmentChangeNode toplevelNode) {
        this.toplevelNode = toplevelNode;
    }

    public void setMacroExpandNode(ElReplacingNode node) {
        this.parentNode = node;
    }

    public boolean isSplice() {
        return isSplice;
    }

    private enum EvalExpressionState {
        NEW, COMPILED
    }

}
