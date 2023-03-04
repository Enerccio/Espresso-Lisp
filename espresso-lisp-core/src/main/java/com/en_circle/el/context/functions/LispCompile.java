package com.en_circle.el.context.functions;

import com.en_circle.el.context.ElContext;
import com.en_circle.el.context.exceptions.ElCompileException;
import com.en_circle.el.lexer.ElLexer;
import com.en_circle.el.lexer.ElLexer.ElLexerState;
import com.en_circle.el.lexer.TokenInfo;
import com.en_circle.el.lexer.TokenType;
import com.en_circle.el.nodes.*;
import com.en_circle.el.runtime.*;
import com.en_circle.el.runtime.natives.InvokeWithArguments;
import com.en_circle.el.runtime.natives.NativeArgument;
import com.en_circle.el.runtime.natives.NativeStaticMethodCompiler;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LispCompile implements InvokeWithArguments {
    public static final String NAME = "compile";
    public static final String SOURCE_ARGUMENT = "source";
    public static final String ENVIRONMENT_ARGUMENT = "env";

    private final ElContext context;

    public LispCompile(ElContext context) {
        this.context = context;
    }

    public ElNativeFunction build() {
        return NativeStaticMethodCompiler.compileFunction(context.getEnvironment(),
                NAME, this, null,
                NativeArgument.withName(SOURCE_ARGUMENT),
                NativeArgument.withName(ENVIRONMENT_ARGUMENT));
    }

    @Override
    public Object invoke(ElClosure closure, Object self, Node callTarget) throws Exception {
        Source source = getSource(closure, callTarget);
        ElEnvironment environment = getEnvironment(closure, callTarget);

        return compile(source, environment);
    }

    private Source getSource(ElClosure closure, Node callTarget) {
        // TODO: support others
        return getFromClosure(closure, SOURCE_ARGUMENT, Source.class, callTarget);
    }

    private ElEnvironment getEnvironment(ElClosure closure, Node callTarget) {
        Object value = getFromClosure(closure, ENVIRONMENT_ARGUMENT, callTarget);
        if (value instanceof ElEnvironment env)
            return env;
        if (value == context.getNil())
            return context.getEnvironment();
        throw new ElCompileException("bad environment for compilation unit", callTarget);
    }

    @Override
    public String getNativeInvocationPlace() {
        return NAME;
    }

    private Object compile(Source source, ElEnvironment environment) {
        ElLexer lexer = new ElLexer(source);

        Object sexpression = sexpression(source, lexer);
        if (sexpression == null) {
            return new ElEmptyRootNode();
        } else {
            ElLexerState state = lexer.mark();
            TokenInfo tokenInfo = lexer.advance();
            while (tokenInfo.getTokenType() == TokenType.WHITESPACE || tokenInfo.getTokenType() == TokenType.COMMENT) {
                tokenInfo = lexer.advance();
            }
            if (tokenInfo.getTokenType() != TokenType.EOF) {
                lexer.seek(state);
            }

            Source remaining = lexer.remaining();
            if (sexpression instanceof ElPair list) {
                ElEvalListNode evalNode = new ElEvalListNode(ElHasSourceInfo.get(sexpression), list, environment);
                ElOpenClosureNode closureNode = new ElOpenClosureNode(ElHasSourceInfo.get(sexpression), evalNode, () -> null);
                evalNode.setMacroExpandNode(closureNode);
                if (remaining == null) {
                    return ElStartEvalChainNode.create(ElHasSourceInfo.get(sexpression), closureNode);
                } else {
                    ElLazyCompileNode lazyCompileNode = ElLazyCompileNode.createLazyCompile(environment,
                            ElStartEvalChainNode.create(ElHasSourceInfo.get(sexpression), closureNode),
                            remaining, ElHasSourceInfo.get(sexpression));
                    evalNode.setParentNode(lazyCompileNode);
                    return lazyCompileNode;
                }
            } else {
                if (remaining == null) {
                    return ElStartEvalChainNode.create(ElHasSourceInfo.get(sexpression),
                            new ElOpenClosureNode(ElHasSourceInfo.get(sexpression),
                                new ElLiteralNode(ElHasSourceInfo.get(sexpression), sexpression), () -> null));
                } else {
                    return ElLazyCompileNode.createLazyCompile(environment,
                            ElStartEvalChainNode.create(ElHasSourceInfo.get(sexpression),
                                new ElOpenClosureNode(ElHasSourceInfo.get(sexpression),
                                        new ElLiteralNode(ElHasSourceInfo.get(sexpression), sexpression),
                                        () -> null)), remaining, ElHasSourceInfo.get(sexpression));
                }
            }

        }
    }

    private Object sexpression(Source source, ElLexer lexer) {
        outer:
        while (true) {
            TokenInfo token = lexer.advance();
            if (token.getTokenType() == TokenType.EOF)
                return null;
            checkErrorToken(token);

            switch (token.getTokenType()) {
                case WHITESPACE -> {
                    continue outer;
                }
                case COMMENT -> {
                    continue outer;
                }
                case QUOTE -> {
                    return quoted(source, lexer, token);
                }
                case LPAREN -> {
                    return list(source, lexer, token);
                }
                case RPAREN -> {
                    unexpectedToken(token);
                }
                case NUMBER -> {
                    // TODO: better numbers
                    return ElHasSourceInfo.withSource(Integer.parseInt(token.getText()), toMetaInfo(source, token));
                }
                case SYMBOL -> {
                    return ElHasSourceInfo.withSource(context.allocateSymbol(token.getText()), toMetaInfo(source, token));
                }
                case STRING -> {
                    return ElHasSourceInfo.withSource(token.getText(), toMetaInfo(source, token));
                }
                case ERROR -> {
                    unexpectedToken(token);
                }
            }
        }
    }

    private Object quoted(Source source, ElLexer lexer, TokenInfo token) {
        Object quotedElement = sexpression(source, lexer);
        Object quote = ElHasSourceInfo.withSource(context.allocateSymbol("quote"), toMetaInfo(source, token));
        ElPair list = ElPair.fromList(Arrays.asList(quote, quotedElement));
        return ElHasSourceInfo.withSource(list, toMetaInfo(source, token));
    }

    private Object list(Source source, ElLexer lexer, TokenInfo startToken) {
        List<Object> elements = new ArrayList<>();

        while (true) {
            ElLexerState cstate = lexer.mark();
            TokenInfo token = lexer.advance();
            if (token.getTokenType() == TokenType.EOF) {
                unexpectedEof();
            }
            checkErrorToken(token);
            if (token.getTokenType() == TokenType.COMMENT)
                continue;
            if (token.getTokenType() == TokenType.WHITESPACE)
                continue;
            if (token.getTokenType() == TokenType.RPAREN) {
                return finalizeList(elements, source, startToken);
            }
            lexer.seek(cstate);
            elements.add(sexpression(source, lexer));
        }
    }

    private Object finalizeList(List<Object> elements, Source source, TokenInfo token) {
        if (elements.isEmpty())
            return ElHasSourceInfo.withSource(context.getNil(), toMetaInfo(source, token));
        return ElHasSourceInfo.withSource(ElPair.fromList(elements), toMetaInfo(source, token));
    }

    private ElNodeMetaInfo toMetaInfo(Source source, TokenInfo token) {
        return ElNodeMetaInfo.atPlace(source, token.getLineno(), token.getCharno());
    }

    private void checkErrorToken(TokenInfo token) {
        if (token.getTokenType() == TokenType.ERROR) {
            throw new ElCompileException(String.format("Wrong token at line %s, character %s (%s) ",
                    token.getLineno() + 1,
                    token.getCharno() + 1,
                    token.getSource()));
        }
    }

    private void unexpectedToken(TokenInfo token) {
        throw new ElCompileException(String.format("Unexpected token %s at line %s, character %s (%s) ",
                token.getTokenType(),
                token.getLineno() + 1,
                token.getCharno() + 1,
                token.getSource()));
    }

    private void unexpectedEof() {
        throw new ElCompileException("Premature end of file");
    }

}
