package com.en_circle.el.runtime;

public class ElNativeFunction extends ElCallable {

    private final String identifier;

    public ElNativeFunction(String identifier, ElClosure closure) {
        super(closure);

        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Object toDisplayString(boolean allowSideEffects) {
        return "<native function " + getIdentifier() + ">";
    }
}
