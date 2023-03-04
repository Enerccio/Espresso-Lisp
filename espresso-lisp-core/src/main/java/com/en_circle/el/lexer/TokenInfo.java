package com.en_circle.el.lexer;

public class TokenInfo  {

    public static final TokenInfo EOF = new TokenInfo();

    private TokenType tokenType = TokenType.EOF;
    StringBuilder textBuilder;
    String text;
    String source;
    private int lineno;
    private int charno;

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public String getText() {
        return text;
    }

    public int getLineno() {
        return lineno;
    }

    public void setLineno(int lineno) {
        this.lineno = lineno;
    }

    public int getCharno() {
        return charno;
    }

    public void setCharno(int charno) {
        this.charno = charno;
    }

    public String getSource() {
        return source;
    }
}
