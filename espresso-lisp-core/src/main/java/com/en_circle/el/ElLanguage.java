package com.en_circle.el;

import com.en_circle.el.context.ElContext;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;

public final class ElLanguage extends TruffleLanguage<ElContext> {

    public static final String INFORMATION = "espresso-lisp";
    private static final LanguageReference<ElLanguage> REFERENCE = LanguageReference.create(ElLanguage.class);
    public static final String ID = "espl";
    public static final String MIME_TYPE = "text/x-espresso-lisp";

    public static ElLanguage get(Node node) {
        return REFERENCE.get(node);
    }

    public ElLanguage() {

    }

    protected ElContext createContext(Env env) {
        return new ElContext(env, this);
    }

    @Override
    protected void initializeContext(ElContext context) throws Exception {
        context.setup();
    }

    @Override
    protected boolean patchContext(ElContext context, Env newEnv) {
        return context.update(newEnv);
    }

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        ElContext context = ElContext.get(null);
        Source source = request.getSource();
        return context.parse(source);
    }

    @Override
    protected void exitContext(ElContext context, ExitMode exitMode, int exitCode) {
        context.runShutdownHooks();
    }

    @Override
    protected Object getScope(ElContext context) {
        return context.getEnvironment();
    }


}
