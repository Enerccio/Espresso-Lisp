package com.en_circle.el.nodes;

import com.en_circle.el.ElLanguage;
import com.en_circle.el.context.ElContext;
import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElNativeFunction;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

public class ElLazyCompileNode extends RootNode implements ElEnvironmentChangeNode {

    public static ElLazyCompileNode createLazyCompile(ElEnvironment environment,
                                                      RootNode node, Source source) {
        ElContext context = ElContext.get(null);
        FrameDescriptor.Builder builder = FrameDescriptor.newBuilder(0);
        ElNativeFunction compile = context.getCompileFunction();
        return new ElLazyCompileNode(node, compile.getCallTarget(), builder.build(),
                source, environment);
    }

    private final RootNode compiledNode;
    private final CallTarget root;
    private final CallTarget continueEval;
    private final Source source;
    private ElEnvironment environment;

    protected ElLazyCompileNode(RootNode compiledNode, CallTarget continueEval, FrameDescriptor descriptor,
                                Source source, ElEnvironment environment) {
        super(ElLanguage.get(null), descriptor);
        this.compiledNode = compiledNode;
        this.root = compiledNode.getCallTarget();
        this.continueEval = continueEval;
        this.source = source;
        this.environment = environment;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        Object returnValue = root.call();
        if (continueEval == null) {
            return returnValue;
        }
        RootNode target = (RootNode) continueEval.call(source, environment);
        return target.getCallTarget().call();
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
        return false;
    }

    @Override
    public SourceSection getSourceSection() {
        return compiledNode.getSourceSection();
    }
}
