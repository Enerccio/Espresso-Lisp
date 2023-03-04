package com.en_circle.el.nodes;

import com.en_circle.el.ElLanguage;
import com.en_circle.el.context.ElCompiledSignature;
import com.en_circle.el.context.exceptions.ElRuntimeException;
import com.en_circle.el.nodes.control.ElReturnException;
import com.en_circle.el.runtime.ElClosure;
import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElPair;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ControlFlowException;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public class ElFunctionEnterNode extends RootNode {

    public static FrameDescriptor.Builder createBaseBuilder() {
        FrameDescriptor.Builder builder = FrameDescriptor.newBuilder(2);
        builder.addSlot(FrameSlotKind.Object, ElNode.SLOT_CLOSURE, null);
        builder.addSlot(FrameSlotKind.Object, ElNode.SLOT_THIS, null);
        return builder;
    }

    private final ElOpenClosureNode node;

    public ElFunctionEnterNode(FrameDescriptor frameDescriptor,
                               ElBlockNode blockNode, ElPair signature,
                               ElEnvironment environment, ElClosure parentClosure) {
        super(ElLanguage.get(null), frameDescriptor);

        ElCompiledSignature.validateSignature(signature, blockNode);
        ElCompiledSignature compiledSignature = new ElCompiledSignature(signature, blockNode);
        ElLoadArgumentsNode argumentsNode = new ElLoadArgumentsNode(blockNode.getMetaInfo(),
                blockNode, compiledSignature);
        this.node = new ElOpenClosureNode(blockNode.getMetaInfo(), argumentsNode, () -> parentClosure);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        try {
            return node.executeGeneric(frame);
        } catch (UnexpectedResultException e) {
            throw new RuntimeException(e);
        } catch (ElReturnException returnException) {
            return returnException.getReturnValue();
        } catch (ControlFlowException otherControlFlows) {
            throw new ElRuntimeException("Unexpected control flow!", otherControlFlows, -1, node);
        }
    }
}
