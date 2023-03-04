package com.en_circle.el.runtime;

public class ElFunction extends ElCallable {

    private final ElEnvironment environment;
    private final ElSymbol name;
    private boolean macro;

    public ElFunction(ElClosure closure, ElEnvironment environment, ElSymbol name) {
        super(closure);
        this.environment = environment;
        this.name = name;

        this.environment.setBinding(name, this);
    }

    @Override
    public Object toDisplayString(boolean allowSideEffects) {
        return (macro ? "Macro" : "Function") + " <" + environment.toDisplayString(allowSideEffects)
                + ":" + name.toDisplayString(allowSideEffects) + ">";
    }

    public String getName() {
        return (macro ? "Macro" : "Function") + " <" + name.toDisplayString(false) + ">";
    }

    @Override
    public boolean isMacro() {
        return macro;
    }

    public void setMacro(boolean macro) {
        this.macro = macro;
    }
}
