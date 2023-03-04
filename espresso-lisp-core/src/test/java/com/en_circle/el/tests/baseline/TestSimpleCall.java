package com.en_circle.el.tests.baseline;

import com.en_circle.el.ElLanguage;
import com.en_circle.el.context.ElContext;
import com.en_circle.el.context.ElSymbolHelper;
import com.en_circle.el.context.functions.LispEnvironmentInfo;
import com.en_circle.el.runtime.ElNativeFunction;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

public class TestSimpleCall {

    public static void main(String[] args) {
        Context context = Context.create(ElLanguage.ID);
        try (context) {
            Value elBindings = context.getBindings(ElLanguage.ID);
            Value func = elBindings.getMember(LispEnvironmentInfo.NAME);
            System.out.println(func);
            System.out.println(func.execute());
        }
    }

}
