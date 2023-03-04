package com.en_circle.el.nodes;

import com.en_circle.el.runtime.ElCallable;
import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElHasSourceInfo;
import com.en_circle.el.runtime.ElPair;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

import java.util.ArrayList;
import java.util.List;

public class ElCallFunction extends ElNode {

    private final ElCallable callable;
    private final List<ElNode> evalList = new ArrayList<>();

    public ElCallFunction(ElNodeMetaInfo metaInfo, ElEnvironment environment,
                          ElCallable callable, Object arguments) {
        super(metaInfo);
        this.callable = callable;

        if (arguments instanceof ElPair list) {
            for (Object rawValue : ElPair.asIterator(list)) {
                Object value = rawValue;
                ElNodeMetaInfo mi = ElHasSourceInfo.get(value);

                if (value instanceof ElPair l) {
                    evalList.add(new ElEvalListNode(mi, l, environment));
                } else {
                    evalList.add(new ElLiteralNode(mi, value));
                }
            }
        }
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
        List<Object> arguments = new ArrayList<>();
        for (ElNode node : evalList) {
            arguments.add(node.executeGeneric(frame));
        }
        return callable.getCallTarget().call(arguments.toArray());
    }
}
