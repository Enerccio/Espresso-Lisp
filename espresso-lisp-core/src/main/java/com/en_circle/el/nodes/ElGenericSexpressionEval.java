package com.en_circle.el.nodes;

import com.en_circle.el.context.exceptions.ElRuntimeException;
import com.en_circle.el.runtime.ElCallable;
import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElHasSourceInfo;
import com.en_circle.el.runtime.ElSymbol;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class ElGenericSexpressionEval extends ElNode {

    private final ElEnvironment environment;
    private final ElResolveFunctionValueNode headNode;
    private ElNode callFunction;
    private final Object arguments;
    private boolean compiled;
    private ElReplacingNode replacingNode;

    public ElGenericSexpressionEval(ElNodeMetaInfo metaInfo,
                                    ElEvalListNode elEvalListNode, ElEnvironment environment,
                                    ElSymbol symbol, Object arguments) {
        super(metaInfo);
        this.environment = environment;
        this.arguments = arguments;
        this.headNode = new ElResolveFunctionValueNode(metaInfo, symbol, environment);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        if (!compiled) {
            Object callable = headNode.executeGeneric(frame);
            if (callable instanceof ElCallable elCallable) {
                if (elCallable.isMacro()) {
                    // TODO
                    ElCallMacro callMacro = new ElCallMacro(metaInfo, environment, elCallable, arguments);
                    Object returnAst = callMacro.executeGeneric(frame);
                    replacingNode.replace(new ElEvalNode(ElHasSourceInfo.get(returnAst), environment, returnAst));
                } else {
                    this.callFunction = new ElCallFunction(metaInfo, environment, elCallable, arguments);
                }
                compiled = true;
            } else {
                throw new ElRuntimeException("Not callable");
            }
        }

        return callFunction.executeGeneric(frame);
    }

    public void setReplacingNode(ElReplacingNode replacingNode) {
        this.replacingNode = replacingNode;
    }

}
