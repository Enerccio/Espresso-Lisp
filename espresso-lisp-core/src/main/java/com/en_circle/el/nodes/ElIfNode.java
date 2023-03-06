package com.en_circle.el.nodes;

import com.en_circle.el.context.ElContext;
import com.en_circle.el.context.TailCall;
import com.en_circle.el.context.TailCallGuard;
import com.en_circle.el.context.exceptions.ElCompileException;
import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElHasSourceInfo;
import com.en_circle.el.runtime.ElPair;
import com.en_circle.el.runtime.ElSymbol;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class ElIfNode extends ElNode {

    private final ElEvalNode test;
    private final ElEvalNode iftrue;
    private final ElEvalNode iffalse;

    public ElIfNode(ElNodeMetaInfo metaInfo, ElEnvironment environment, Object arguments) {
        super(metaInfo);
        if (!(arguments instanceof ElPair pair)) {
            throw new ElCompileException("if with incorrect number of arguments", this);
        }
        test = new ElEvalNode(ElHasSourceInfo.get(arguments), environment, ElPair.car(pair));
        Object ifValue = ElPair.nth(arguments, 1);
        iftrue = new ElEvalNode(ElHasSourceInfo.get(ifValue), environment, ifValue);
        Object elseValue = ElPair.nth(arguments, 2);
        iffalse = new ElEvalNode(ElHasSourceInfo.get(elseValue), environment, elseValue);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        ElSymbol nil = ElContext.get(this).getNil();

        Object testResult;
        try (TailCallGuard ignored = new TailCallGuard(TailCall.NO)) {
            testResult = test.executeGeneric(frame);
        }
        if (testResult != nil) {
            return iftrue.executeGeneric(frame);
        } else {
            return iffalse.executeGeneric(frame);
        }
    }
}
