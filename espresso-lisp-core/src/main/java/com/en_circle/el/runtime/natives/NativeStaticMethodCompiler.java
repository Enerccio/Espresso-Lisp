package com.en_circle.el.runtime.natives;

import com.en_circle.el.ElLanguage;
import com.en_circle.el.context.ArgumentsToClosure;
import com.en_circle.el.context.exceptions.ElArgumentsException;
import com.en_circle.el.context.exceptions.ElInvocationTargetException;
import com.en_circle.el.nodes.ElLoadArgumentsNode;
import com.en_circle.el.nodes.ElNode;
import com.en_circle.el.nodes.ElNodeMetaInfo;
import com.en_circle.el.nodes.ElOpenClosureNode;
import com.en_circle.el.runtime.ElClosure;
import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElNativeFunction;
import com.en_circle.el.runtime.ElSymbol;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.source.SourceSection;

import java.util.function.Supplier;

public class NativeStaticMethodCompiler {

    public static ElNativeFunction compileNoArgsFunction(ElEnvironment environment,
                                                         String identifier, ThrowingRunnable runnable) {
        return compileNoArgsFunction(environment, identifier,
                NativeWrappers.wrap(runnable));
    }

    public static ElNativeFunction compileNoArgsFunction(ElEnvironment environment,
                                                         String identifier, Runnable runnable) {
        return compileNoArgsFunction(environment, identifier,
                NativeWrappers.wrapVoid(runnable));
    }

    public static ElNativeFunction compileNoArgsFunction(ElEnvironment environment,
                                                         String identifier, ThrowingSupplier<Object> runnable) {
        return compileNoArgsFunction(environment, identifier,
                NativeWrappers.wrap(runnable));
    }

    public static ElNativeFunction compileNoArgsFunction(ElEnvironment environment,
                                                         String identifier, Supplier<Object> runnable) {
        ElNativeFunction nativeFunction = new ElNativeFunction(identifier, null);
        ElNode dispatchNode = new ElNode(ElNodeMetaInfo.nativeMetaInfo(identifier)) {

            @Override
            @TruffleBoundary
            public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
                return runnable.get();
            }
        };
        RootNode rootNode = compileNativeFunction(dispatchNode, nativeFunction);
        nativeFunction.setCallTarget(rootNode.getCallTarget());
        return nativeFunction;
    }

    public static ElNativeFunction compileSingleArgument(ElEnvironment environment, String identifier,
                                                   InvokeSingleArgument invokeSingle, NativeArgument nativeArgument) {
        ElNativeFunction nativeFunction = new ElNativeFunction(identifier, null);
        ElNode dispatchNode = new ElNode(ElNodeMetaInfo.nativeMetaInfo(identifier)) {

            @Override
            @TruffleBoundary
            public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
                ElClosure closure = (ElClosure) frame.getObject(SLOT_CLOSURE);
                return NativeWrappers.wrap(() -> {
                    Object value = closure.getBinding(nativeArgument.getSymbol());
                    return invokeSingle.invoke(value);
                }).get();
            }
        };
        RootNode rootNode = compileNativeFunction(dispatchNode, nativeFunction, nativeArgument);
        nativeFunction.setCallTarget(rootNode.getCallTarget());
        return nativeFunction;
    }

    public static ElNativeFunction compileFunction(ElEnvironment environment, String identifier,
                                                   InvokeWithArguments invokeWithArguments, NativeArgument... nativeArguments) {
        ElNativeFunction nativeFunction = new ElNativeFunction(identifier, null);
        ElNode dispatchNode = new ElNode(ElNodeMetaInfo.nativeMetaInfo(identifier)) {

            @Override
            @TruffleBoundary
            public Object executeGeneric(VirtualFrame frame) throws UnexpectedResultException {
                ElClosure closure = (ElClosure) frame.getObject(SLOT_CLOSURE);
                Object self = frame.getObject(SLOT_THIS);
                return NativeWrappers.wrap(() -> invokeWithArguments.invoke(closure, self, this)).get();
            }
        };
        RootNode rootNode = compileNativeFunction(dispatchNode, nativeFunction, nativeArguments);
        nativeFunction.setCallTarget(rootNode.getCallTarget());
        return nativeFunction;
    }

    private static RootNode compileNativeFunction(ElNode dispatchNode, ElNativeFunction nativeFunction,
                                                  NativeArgument... args) {
        ArgumentsToClosure argumentsToClosure = (closure, arguments) -> {
            if (arguments.length != args.length)
                throw new ElArgumentsException("Arity exception", dispatchNode);
            for (int ix = 0; ix < args.length; ix++) {
                NativeArgument nativeArgument = args[ix];
                Object argument = arguments[ix];
                ElSymbol symbol = nativeArgument.getSymbol();
                closure.setBinding(symbol, argument);
            }
        };
        FrameDescriptor.Builder frameBuilder = FrameDescriptor.newBuilder(2);
        frameBuilder.addSlot(FrameSlotKind.Object, ":closure", ElNode.SLOT_CLOSURE);
        frameBuilder.addSlot(FrameSlotKind.Object, ":this", ElNode.SLOT_THIS);

        return new RootNode(ElLanguage.get(null), frameBuilder.build()) {

            private final ElNode node;

            {
                node = new ElOpenClosureNode(ElNodeMetaInfo.nativeMetaInfo(nativeFunction.getIdentifier()),
                        new ElLoadArgumentsNode(ElNodeMetaInfo.nativeMetaInfo(nativeFunction.getIdentifier()), dispatchNode,
                                argumentsToClosure), nativeFunction::getClosure);
            }

            @Override
            public Object execute(VirtualFrame frame) {
                try {
                    return node.executeGeneric(frame);
                } catch (UnexpectedResultException e) {
                    throw new ElInvocationTargetException(e);
                }
            }

            @Override
            public String getQualifiedName() {
                return nativeFunction.getIdentifier();
            }

            @Override
            public String getName() {
                return nativeFunction.getIdentifier();
            }

            @Override
            public boolean isInternal() {
                return false;
            }

            @Override
            public SourceSection getSourceSection() {
                return null;
            }
        };
    }

}
