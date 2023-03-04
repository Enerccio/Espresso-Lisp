package com.en_circle.el.runtime;

public class ElFunction extends ElCallable {

    private final ElEnvironment environment;
    private final ElSymbol name;

    public ElFunction(ElClosure closure, ElEnvironment environment, ElSymbol name) {
        super(closure);
        this.environment = environment;
        this.name = name;

        this.environment.setBinding(name, this);
    }

    @Override
    public Object toDisplayString(boolean allowSideEffects) {
        return "Function <" + environment.toDisplayString(allowSideEffects)
                + ":" + name.toDisplayString(allowSideEffects) + ">";
    }

    public String getName() {
        return "Function <" + name.toDisplayString(false) + ">";
    }
}
