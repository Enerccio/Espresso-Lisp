package com.en_circle.el;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

import java.io.File;

public class ElMain {

    public static void main(String[] args) throws Exception {
        Context context = Context.newBuilder(ElLanguage.ID).build();
        Source source = Source.newBuilder(ElLanguage.ID, new File("/src/self/espresso-lisp/test.espl")).build();
        long time = System.nanoTime();
        System.out.println(context.eval(source));
        long took = System.nanoTime() - time;
        System.out.println("Took " + took / 1000000.0 + " ms");
    }

}
