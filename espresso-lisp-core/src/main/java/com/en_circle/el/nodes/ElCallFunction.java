package com.en_circle.el.nodes;

import com.en_circle.el.context.TailCall;
import com.en_circle.el.context.TailCallGuard;
import com.en_circle.el.nodes.control.ElTailCallException;
import com.en_circle.el.runtime.*;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import java.util.ArrayList;
import java.util.List;

public class ElCallFunction extends ElNode {

    private final ElCallable callable;
    private final List<ElEvalNode> evalList = new ArrayList<>();

    public ElCallFunction(ElNodeMetaInfo metaInfo, ElEnvironment environment,
                          ElCallable callable, Object arguments) {
        super(metaInfo);
        this.callable = callable;

        if (arguments instanceof ElPair list) {
            for (Object value : ElPair.asIterator(list)) {
                ElNodeMetaInfo mi = ElHasSourceInfo.get(value);
                evalList.add(new ElEvalNode(mi, environment, value));
            }
        }
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        List<Object> arguments = new ArrayList<>();
        for (ElEvalNode node : evalList) {
            try (TailCallGuard ignored = new TailCallGuard(TailCall.NO)) {
                Object value = node.executeGeneric(frame);
                arguments.add(value);
            }
        }

        Object[] args = arguments.toArray();
        if (TailCallGuard.STATE.get() == TailCall.YES && callable.equals(TailCallGuard.CURRENT_FUNCTION.get())) {
            throw new ElTailCallException(args);
        }

        for (;;) {
            try (TailCallGuard ignored = new TailCallGuard(TailCall.YES, callable instanceof ElFunction ?
                    ((ElFunction) callable) : null)) {
                return callable.getCallTarget().call(args);
            } catch (ElTailCallException tailCallException) {
                args = tailCallException.getNewArguments();
            }
        }
    }
}
