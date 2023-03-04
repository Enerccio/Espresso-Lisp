package com.en_circle.el.nodes;

import com.en_circle.el.runtime.ElCallable;
import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElPair;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import java.util.ArrayList;
import java.util.List;

public class ElCallMacro extends ElNode {

    private final ElCallable callable;
    private final List<Object> argList = new ArrayList<>();

    public ElCallMacro(ElNodeMetaInfo metaInfo, ElEnvironment environment,
                       ElCallable callable, Object arguments) {
        super(metaInfo);
        this.callable = callable;

        if (arguments instanceof ElPair list) {
            for (Object value : ElPair.asIterator(list)) {
                argList.add(value);
            }
        }
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        return callable.getCallTarget().call(argList.toArray());
    }
}
