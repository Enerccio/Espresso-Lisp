package com.en_circle.el.context;

import com.en_circle.el.ElLanguage;
import com.en_circle.el.context.exceptions.ElCompileException;
import com.en_circle.el.context.functions.*;
import com.en_circle.el.nodes.ElBlockNode;
import com.en_circle.el.nodes.ElFunctionEnterNode;
import com.en_circle.el.nodes.ElNodeMetaInfo;
import com.en_circle.el.runtime.*;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.instrumentation.AllocationReporter;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ElContext {

    private static final ContextReference<ElContext> REFERENCE = ContextReference.create(ElLanguage.class);

    public static ElContext get(Node node) {
        return REFERENCE.get(node);
    }

    private AllocationReporter reporter;
    private ElEnvironment builtins;
    private final ElLanguage language;
    private final Map<String, ElSymbol> internSymbolMap = new ConcurrentHashMap<>();
    private final AtomicInteger envCounter = new AtomicInteger(0);
    private final AtomicInteger gensymCounter = new AtomicInteger(0);
    private ElSymbol nil;

    public ElContext(Env env, ElLanguage elLanguage) {
        this.language = elLanguage;
        this.reporter = env.lookup(AllocationReporter.class);
    }

    public boolean update(Env newEnv) {
        this.reporter = newEnv.lookup(AllocationReporter.class);

        return true;
    }

    public void setup() {
        builtins = ElShapeFactory.allocateEnvironment(this, null);
        nil = ElSymbolHelper.getSymbol("NIL");

        addToBuiltin(new LispEnvironmentInfo(this).build());
        addToBuiltin(new LispCompile(this).build());
        addToBuiltin(new LispPrintln(this).build());
        addToBuiltin(new LispCar(this).build());
        addToBuiltin(new LispCdr(this).build());
    }

    private void addToBuiltin(ElNativeFunction nativeFunction) {
        builtins.setBinding(allocateSymbol(nativeFunction.getIdentifier()), nativeFunction);
    }

    public void runShutdownHooks() {

    }

    public CallTarget parse(Source source) throws Exception {
        ElNativeFunction compileFunction = getCompileFunction();
        RootNode compilationUnit = (RootNode) compileFunction.getCallTarget().call(source, createEnvironment());
        return compilationUnit.getCallTarget();
    }

    private void loadBuiltin(URL builtin) throws Exception {
        Source source = Source.newBuilder(ElLanguage.ID, builtin).build();
        ElNativeFunction compileFunction = getCompileFunction();
        RootNode compilationUnit = (RootNode) compileFunction.getCallTarget().call(source, builtins);
        compilationUnit.getCallTarget().call();
    }

    private ElEnvironment createEnvironment() {
        ElEnvironment environment = ElShapeFactory.allocateEnvironment(this,
                "unnamed environment " + envCounter.getAndIncrement());
        copyBuiltins(environment);
        return environment;
    }

    private void copyBuiltins(ElEnvironment environment) {
        for (ElSymbol symbol : builtins.getBindings()) {
            environment.setBinding(symbol, builtins.getBinding(symbol));
        }
    }

    public AllocationReporter getReporter() {
        return reporter;
    }

    public ElSymbol allocateSymbol(String name) {
        if (name.equals("nil") && nil != null)
            return nil;
        return internSymbolMap.computeIfAbsent(name, ElSymbol::new);
    }

    public ElSymbol allocateGensym() {
        return allocateGensym(gensymCounter.getAndIncrement());
    }

    public ElSymbol allocateGensym(int numberOverride) {
        return new ElSymbol(String.format("gensym %i", numberOverride), true);
    }

    public ElSymbol getNil() {
        return nil;
    }

    public ElEnvironment getEnvironment() {
        return builtins;
    }

    public ElNativeFunction getCompileFunction() {
        return (ElNativeFunction) builtins.getBinding(allocateSymbol(LispCompile.NAME));
    }

    public ElSymbol defineFunction(ElNodeMetaInfo metaInfo, ElEnvironment environment, ElClosure parentClosure,
                                   Object arguments) {
        if (!(arguments instanceof ElPair))
            throw new ElCompileException("Bad define! signature");
        Object type = ElPair.car(arguments);
        arguments = ElPair.cdr(arguments);
        Object name = ElPair.car(arguments);
        arguments = ElPair.cdr(arguments);
        Object args = ElPair.car(arguments);
        arguments = ElPair.cdr(arguments);
        if (!(type instanceof ElSymbol typeSymbol))
            throw new ElCompileException("Bad define! signature");
        if (!(name instanceof ElSymbol nameSymbol))
            throw new ElCompileException("Bad define! signature");
        if (!(args instanceof ElPair signature))
            throw new ElCompileException("Bad define! signature");

        // TODO macros

        ElNodeMetaInfo newMetaInfo = ElHasSourceInfo.get(arguments);
        ElBlockNode blockNode = new ElBlockNode(newMetaInfo, environment, arguments);
        ElFunctionEnterNode functionEnterNode = new ElFunctionEnterNode(ElFunctionEnterNode.createBaseBuilder().build(),
                blockNode, signature, environment, parentClosure);
        ElFunction function = new ElFunction(parentClosure, environment, nameSymbol);
        function.setCallTarget(functionEnterNode.getCallTarget());
        return nameSymbol;
    }
}
