package com.en_circle.el.context;

import com.en_circle.el.ElLanguage;
import com.en_circle.el.context.exceptions.ElCompileException;
import com.en_circle.el.context.functions.*;
import com.en_circle.el.nodes.ElBlockNode;
import com.en_circle.el.nodes.ElFunctionEnterNode;
import com.en_circle.el.nodes.ElNodeMetaInfo;
import com.en_circle.el.runtime.*;
import com.en_circle.el.utils.IOUtils;
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
    private ElEnvironment builtinsInternal;
    private final ElLanguage language;
    private final Map<String, ElSymbol> internSymbolMap = new ConcurrentHashMap<>();
    private final AtomicInteger envCounter = new AtomicInteger(0);
    private final AtomicInteger gensymCounter = new AtomicInteger(0);
    private ElSymbol nil;
    private ElSymbol t;
    private final ElMathContext mathContext = new ElMathContext(this);

    public ElContext(Env env, ElLanguage elLanguage) {
        this.language = elLanguage;
        this.reporter = env.lookup(AllocationReporter.class);
    }

    public boolean update(Env newEnv) {
        this.reporter = newEnv.lookup(AllocationReporter.class);

        return true;
    }

    public void setup() throws Exception {
        builtins = ElShapeFactory.allocateEnvironment(this, null);
        builtinsInternal = ElShapeFactory.allocateEnvironment(this, "Internal Builtins");
        builtins.setBinding(allocateSymbol("int"), builtinsInternal);
        nil = ElSymbolHelper.getSymbol("nil");
        t = ElSymbolHelper.getSymbol("t");

        addToBuiltin(new LispCompile(this).build(), builtinsInternal);

        // required for defmacro.espl
        addToBuiltin(new LispCar(this).build(), builtins);
        addToBuiltin(new LispCdr(this).build(), builtins);

        addToBuiltin(ElContext.class.getResource("/stdlib/defmacro.espl"), builtins);
        addToBuiltin(ElContext.class.getResource("/stdlib/defun.espl"), builtins);

        addToBuiltin(new LispEnvironmentInfo(this).build(), builtins);
        addToBuiltin(new LispGensym(this).build(), builtins);
        addToBuiltin(new LispPrintln(this).build(), builtins);
        addToBuiltin(new LispCons(this).build(), builtins);
        addToBuiltin(new LispPlus(this).build(), builtins);
        addToBuiltin(new LispMinus(this).build(), builtins);
        addToBuiltin(new LispEqualSign(this).build(), builtins);

        addToBuiltin(ElContext.class.getResource("/stdlib/list.espl"), builtins);
    }

    private void addToBuiltin(URL resource, ElEnvironment environment) throws Exception {
        loadBuiltin(resource, environment);
    }

    private void addToBuiltin(ElNativeFunction nativeFunction, ElEnvironment environment) {
        environment.setBinding(allocateSymbol(nativeFunction.getIdentifier()), nativeFunction);
    }

    public void runShutdownHooks() {

    }

    public CallTarget parse(Source source) {
        ElNativeFunction compileFunction = getCompileFunction();
        RootNode compilationUnit = (RootNode) compileFunction.getCallTarget().call(source, createEnvironment());
        return compilationUnit.getCallTarget();
    }

    private void loadBuiltin(URL builtinComponent, ElEnvironment builtins) throws Exception {
        String data = IOUtils.fromURL(builtinComponent);
        Source source = Source.newBuilder(ElLanguage.ID, data, builtinComponent.toURI().getPath()).build();
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
        if (name.equals("t") && t != null)
            return t;
        return internSymbolMap.computeIfAbsent(name, ElSymbol::new);
    }

    public ElSymbol allocateGensym() {
        return allocateGensym(gensymCounter.getAndIncrement());
    }

    public ElSymbol allocateGensym(Object numberOverride) {
        if (numberOverride == nil)
            return allocateGensym();
        return new ElSymbol(String.format("gensym %s", numberOverride), true);
    }

    public ElSymbol getNil() {
        return nil;
    }

    public ElSymbol getT() {
        return t;
    }

    public ElEnvironment getEnvironment() {
        return builtins;
    }

    public ElNativeFunction getCompileFunction() {
        return (ElNativeFunction) builtinsInternal.getBinding(allocateSymbol(LispCompile.NAME));
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
        if (!(args instanceof ElPair) && !(args instanceof ElSymbol))
            throw new ElCompileException("Bad define! signature");

        ElNodeMetaInfo newMetaInfo = ElHasSourceInfo.get(arguments);
        ElBlockNode blockNode = new ElBlockNode(newMetaInfo, environment, arguments);
        ElFunctionEnterNode functionEnterNode = new ElFunctionEnterNode(ElFunctionEnterNode.createBaseBuilder().build(),
                blockNode, args, environment, parentClosure);
        ElFunction function = new ElFunction(parentClosure, environment, nameSymbol);
        function.setCallTarget(functionEnterNode.getCallTarget());
        functionEnterNode.setFunction(function);

        if ("macro".equals(typeSymbol.getName())) {
            function.setMacro(true);
        } else if ("function".equals(typeSymbol.getName())) {
            function.setMacro(false);
        } else {
            throw new ElCompileException("Bad define! signature");
        }

        return nameSymbol;
    }

    public ElMathContext getMathContext() {
        return mathContext;
    }
}
