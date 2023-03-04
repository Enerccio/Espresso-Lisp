package com.en_circle.el.nodes;

import com.en_circle.el.ElLanguage;
import com.en_circle.el.context.ElContext;
import com.en_circle.el.context.exceptions.ElRuntimeException;
import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElNativeFunction;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

public class ElLazyCompileNode extends RootNode implements ElEnvironmentChangeNode {

    public static ElLazyCompileNode createLazyCompile(ElEnvironment environment,
                                                      RootNode node, Source source, ElNodeMetaInfo metaInfo) {
        ElContext context = ElContext.get(null);
        FrameDescriptor.Builder builder = FrameDescriptor.newBuilder(0);
        ElNativeFunction compile = context.getCompileFunction();
        return new ElLazyCompileNode(metaInfo, node, compile.getCallTarget(), builder.build(),
                source, environment);
    }

    private final InternalCallNode internalCallNode;
    private final CallTarget continueEval;
    private final ElNodeMetaInfo metaInfo;
    private final Source source;
    private ElEnvironment environment;


    protected ElLazyCompileNode(ElNodeMetaInfo metaInfo, RootNode compiledNode, CallTarget continueEval, FrameDescriptor descriptor,
                                Source source, ElEnvironment environment) {
        super(ElLanguage.get(null), descriptor);
        this.continueEval = continueEval;
        this.source = source;
        this.environment = environment;
        this.metaInfo = metaInfo;
        this.internalCallNode = new InternalCallNode(metaInfo, compiledNode.getCallTarget());
    }

    @Override
    public Object execute(VirtualFrame frame) {
        try {
            Object returnValue = internalCallNode.executeGeneric(frame);
            if (continueEval == null) {
                return returnValue;
            }
            RootNode target = (RootNode) continueEval.call(source, environment);
            return target.getCallTarget().call();
        } catch (UnexpectedResultException e) {
            throw new ElRuntimeException(e);
        }
    }

    @Override
    public void setNewEnvironment(ElEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public String getQualifiedName() {
        return "eval";
    }

    @Override
    public String getName() {
        return "eval";
    }

    @Override
    public boolean isInternal() {
        return true;
    }

    @Override
    public SourceSection getSourceSection() {
        return metaInfo.getSourceSection();
    }

    private static class InternalCallNode extends ElNode {

        private final CallTarget callTarget;

        public InternalCallNode(ElNodeMetaInfo metaInfo, CallTarget callTarget) {
            super(metaInfo);
            this.callTarget = callTarget;
        }

        @Override
        @TruffleBoundary
        public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
            return callTarget.call();
        }
    }
}
