package com.en_circle.el;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

import java.io.File;

public class ElMain {

    public static void main(String[] args) throws Exception {
        Context context = Context.newBuilder(ElLanguage.ID).build();
        Source source = Source.newBuilder(ElLanguage.ID, new File("/src/self/espresso-lisp/test.espl")).build();
        System.out.println(context.eval(source));
    }

}
