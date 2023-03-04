package com.en_circle.el.context;

import com.en_circle.el.ElLanguage;
import com.en_circle.el.context.functions.LispCompile;
import com.en_circle.el.context.functions.LispEnvironmentInfo;
import com.en_circle.el.context.functions.LispPrintln;
import com.en_circle.el.runtime.ElEnvironment;
import com.en_circle.el.runtime.ElNativeFunction;
import com.en_circle.el.runtime.ElSymbol;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.instrumentation.AllocationReporter;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;

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
        builtins = ElShapeFactory.allocateEnvironment(this);
        nil = ElSymbolHelper.getSymbol("NIL");

        addToBuiltin(new LispEnvironmentInfo(this).build());
        addToBuiltin(new LispCompile(this).build());
        addToBuiltin(new LispPrintln(this).build());
    }

    private void addToBuiltin(ElNativeFunction nativeFunction) {
        builtins.setBinding(allocateSymbol(nativeFunction.getIdentifier()), nativeFunction);
    }

    public void runShutdownHooks() {

    }

    public CallTarget parse(Source source) throws Exception {
        ElNativeFunction compileFunction = getCompileFunction();
        RootNode compilationUnit = (RootNode) compileFunction.getCallTarget().call(source, getEnvironment());
        return compilationUnit.getCallTarget();
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
}
