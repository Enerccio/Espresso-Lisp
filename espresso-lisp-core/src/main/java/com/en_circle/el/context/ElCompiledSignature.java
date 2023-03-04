package com.en_circle.el.context;

import com.en_circle.el.context.exceptions.ElArgumentsException;
import com.en_circle.el.context.exceptions.ElCompileException;
import com.en_circle.el.nodes.ElNode;
import com.en_circle.el.runtime.ElClosure;
import com.en_circle.el.runtime.ElPair;
import com.en_circle.el.runtime.ElSymbol;

import java.util.ArrayList;
import java.util.List;

public class ElCompiledSignature implements ArgumentsToClosure {

    private boolean compiled = false;
    private final Object signature;
    private final ElNode parentNode;

    private final List<ElSymbol> requiredArguments = new ArrayList<>();

    public ElCompiledSignature(Object signature, ElNode parentNode) {
        this.parentNode = parentNode;
        this.signature = signature;
    }

    @Override
    public void setSymbolBindings(ElClosure closure, Object[] arguments) {
        if (!compiled) {
            compile();
        }
        if (requiredArguments.size() != arguments.length) {
            throw new ElArgumentsException("Bad arity", parentNode);
        }

        for (int ix=0; ix<arguments.length; ix++) {
            ElSymbol symbol = requiredArguments.get(ix);
            Object value = arguments[ix];
            closure.setBinding(symbol, value);
        }
    }

    private void compile() {
        compiled = true;
        if (ElContext.get(null).getNil() == signature) {
            return;
        }
        ElPair pair = (ElPair) signature;
        for (Object s : ElPair.asIterator(pair)) {
            ElSymbol symbol = (ElSymbol) s;
            requiredArguments.add(symbol);
        }
    }

    public static void validateSignature(Object signature, ElNode node) {
        if (ElContext.get(null).getNil() == signature) {
            return;
        }

        if (signature instanceof ElPair p) {
            boolean alLValid = true;
            for (Object s : ElPair.asIterator(p)) {
                if (!(s instanceof ElSymbol)) {
                    alLValid = false;
                }
            }
            if (alLValid)
                return;
        }

        throw new ElCompileException("Wrong signature", node);
    }
}
