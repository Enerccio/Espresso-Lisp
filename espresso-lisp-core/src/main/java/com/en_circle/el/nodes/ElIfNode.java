package com.en_circle.el.nodes;

import com.en_circle.el.context.ElContext;
import com.en_circle.el.context.exceptions.ElCompileException;
import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElHasSourceInfo;
import com.en_circle.el.runtime.ElPair;
import com.en_circle.el.runtime.ElSymbol;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class ElIfNode extends ElNode {

    private final ElEnvironment environment;
    private final ElPair arguments;
    private ElEvalNode test;
    private ElEvalNode iftrue;
    private ElEvalNode iffalse;

    public ElIfNode(ElNodeMetaInfo metaInfo, ElEnvironment environment, Object arguments) {
        super(metaInfo);
        if (!(arguments instanceof ElPair pair)) {
            throw new ElCompileException("if with incorrect number of arguments", this);
        }
        this.arguments = pair;
        this.environment = environment;
        test = new ElEvalNode(ElHasSourceInfo.get(arguments), environment, ElPair.car(pair));
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        ElSymbol nil = ElContext.get(this).getNil();

        Object testResult = test.executeGeneric(frame);
        if (testResult != nil) {
            if (iftrue == null) {
                Object ifValue = ElPair.nth(arguments, 1);
                iftrue = new ElEvalNode(ElHasSourceInfo.get(ifValue), environment, ifValue);
            }
            return iftrue.executeGeneric(frame);
        } else {
            if (iffalse == null) {
                Object ifValue = ElPair.nth(arguments, 2);
                iffalse = new ElEvalNode(ElHasSourceInfo.get(ifValue), environment, ifValue);
            }
            return iffalse.executeGeneric(frame);
        }
    }
}
