package com.en_circle.el.lexer;


import com.oracle.truffle.api.source.Source;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class ElLexer {
    private static final Pattern whitespace = Pattern.compile("[\\r\\n\\t \\x0c\\x0a]");

    private static final Pattern identifiers = Pattern.compile("[\\-_A-Za-z*$&=+/!0-9^?:%<>~.]");
    private static final Pattern digits = Pattern.compile("^[0-9]*$");
    private Set<Character> reserved = new HashSet<>(Arrays.asList('#', ',', '.', '|', '@', '[', ']', '{', '}', '`'));

    private final Source source;
    private int position;
    private int lineno;
    private int charno;
    private CharSequence characters;
    private TokenInfo lastToken;

    public ElLexer(Source source) {
        this.source = source;
        this.characters = source.getCharacters();
    }

    public ElLexerState mark() {
        return new ElLexerState(position, lineno, charno);
    }

    public void seek(ElLexerState state) {
        this.charno = state.charno();
        this.lineno = state.lineno();
        this.position = state.position();
        this.lastToken = null;
    }

    public Source remaining() {
        if (position >= characters.length())
            return null;
        return source.subSource(position, characters.length() - position);
    }

    public TokenInfo advance() {
        TokenInfo tokenInfo = null;
        boolean escape = false;

        outer:
        while (true) {
            if (tokenInfo == null) {
                int c = readNextChar();
                if (c == -1) {
                    break;
                }

                if (whitespace.asPredicate().test(Character.toString((char) c))) {
                    tokenInfo = mkToken();
                    tokenInfo.setTokenType(TokenType.WHITESPACE);
                } else if (c == '(') {
                    tokenInfo = mkLightToken();
                    tokenInfo.setTokenType(TokenType.LPAREN);
                    break;
                } else if (c == ')') {
                    tokenInfo = mkLightToken();
                    tokenInfo.setTokenType(TokenType.RPAREN);
                    break;
                } else if (c == '\'') {
                    tokenInfo = mkToken();
                    tokenInfo.setTokenType(TokenType.QUOTE);
                    break;
                } else if (c == '"') {
                    tokenInfo = mkToken();
                    tokenInfo.setTokenType(TokenType.STRING);
                } else if (c == ';') {
                    tokenInfo = mkToken();
                    tokenInfo.setTokenType(TokenType.COMMENT);
                } else if (reserved.contains((char) c)) {
                    tokenInfo = mkLightToken();
                    tokenInfo.setTokenType(TokenType.ERROR);
                    break;
                } else if (identifiers.asPredicate().test(Character.toString((char) c))) {
                    tokenInfo = mkToken();
                    tokenInfo.setTokenType(TokenType.SYMBOL);
                    tokenInfo.textBuilder.append((char) c);
                } else {
                    tokenInfo = mkLightToken();
                    tokenInfo.setTokenType(TokenType.ERROR);
                    break;
                }
            } else {
                int c = peekNextChar(0);

                switch (tokenInfo.getTokenType()) {
                    case WHITESPACE -> {
                        if (c == -1) {
                            break outer;
                        }
                        if (!whitespace.asPredicate().test(Character.toString((char) c))) {
                            break outer;
                        }
                        tokenInfo.textBuilder.append((char) c);
                    }
                    case SYMBOL -> {
                        if (c == -1) {
                            break outer;
                        }
                        if (whitespace.asPredicate().test(Character.toString((char) c))) {
                            break outer;
                        } else if (reserved.contains((char) c)) {
                            break outer;
                        } else if (!identifiers.asPredicate().test(Character.toString((char) c))) {
                            break outer;
                        }
                        tokenInfo.textBuilder.append((char) c);
                    }
                    case COMMENT -> {
                        if (c == -1) {
                            break outer;
                        }
                        if (c == '\n') {
                            break outer;
                        }
                        tokenInfo.textBuilder.append((char) c);
                    }
                    case STRING -> {
                        if (c == -1) {
                            tokenInfo.setTokenType(TokenType.ERROR);
                            break outer;
                        } else if (c == '\\' && !escape) {
                            escape = true;
                        } else if (escape) {
                            if (c == '\\') {
                                tokenInfo.textBuilder.append("\\");
                            } else if (c == 'n') {
                                tokenInfo.textBuilder.append("\n");
                            } else if (c == 't') {
                                tokenInfo.textBuilder.append("\t");
                            } else if (c == 'r') {
                                tokenInfo.textBuilder.append("\r");
                            } else if (c == '"') {
                                tokenInfo.textBuilder.append("\\");
                            } else {
                                tokenInfo.textBuilder.append((char) c);
                            }
                            escape = false;
                        } else if (c == '\"') {
                            readNextChar();
                            break outer;
                        } else {
                            tokenInfo.textBuilder.append((char) c);
                        }
                    }
                }

                c = readNextChar();
                if (c == -1) {
                    break;
                }
            }
        }
        if (tokenInfo != null) {
            if (tokenInfo.textBuilder != null)
                tokenInfo.text = tokenInfo.textBuilder.toString();

            if (tokenInfo.getTokenType() == TokenType.SYMBOL) {
                if (tokenInfo.text.startsWith(":") || tokenInfo.text.endsWith(":")) {
                    tokenInfo.setTokenType(TokenType.ERROR);
                } else {
                    tryMatchNumber(tokenInfo.text, tokenInfo);
                }
            }
            lastToken = tokenInfo;
        }
        return lastToken;
    }

    private void tryMatchNumber(String text, TokenInfo tokenInfo) {
        // TODO: more shit
        if (digits.matcher(text).matches()) {
            if (!text.startsWith("0") || text.length() == 1) {
                tokenInfo.setTokenType(TokenType.NUMBER);
            }
        }
    }

    private TokenInfo mkLightToken() {
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setCharno(charno);
        tokenInfo.setLineno(lineno);
        tokenInfo.source = source.getName();
        return tokenInfo;
    }

    private TokenInfo mkToken() {
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.textBuilder = new StringBuilder();
        tokenInfo.setCharno(charno);
        tokenInfo.setLineno(lineno);
        tokenInfo.source = source.getName();
        return tokenInfo;
    }

    private int readNextChar() {
        if (position >= characters.length()) {
            lastToken = TokenInfo.EOF;
            return -1;
        }
        char c = characters.charAt(position++);
        ++charno;
        if (c == '\n') {
            ++lineno;
            charno = 0;
        }
        return c;
    }

    private int peekNextChar(int advance) {
        if (position+advance >= characters.length()) {
            return -1;
        }
        return characters.charAt(position+advance);
    }

    public TokenInfo getToken() {
        return lastToken;
    }

    public record ElLexerState(int position, int lineno, int charno) {

    }
}
