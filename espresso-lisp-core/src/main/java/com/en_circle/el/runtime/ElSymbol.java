package com.en_circle.el.runtime;

import com.en_circle.el.lexer.ElLexer;
import com.en_circle.el.nodes.ElNodeMetaInfo;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import java.util.Objects;

@ExportLibrary(InteropLibrary.class)
public class ElSymbol implements ElObject, ElHasSourceInfo, Comparable<ElSymbol> {

    private final String name;
    private final boolean anonymous;
    private final boolean isCompound;
    private final boolean garbage;
    private ElNodeMetaInfo metaInfo;

    public ElSymbol(String name) {
        this(name, false);
    }

    public ElSymbol(String name, boolean anonymous) {
        Objects.requireNonNull(name);

        this.name = name;
        this.anonymous = anonymous;
        this.isCompound = name.contains(":");
        this.garbage = ElLexer.notIdentifiers.matcher(name).find();
    }

    @Override
    public boolean equals(Object o) {
        if (anonymous) {
            return o == this;
        }

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ElSymbol elSymbol = (ElSymbol) o;
        return Objects.equals(name, elSymbol.name);
    }

    @Override
    public int hashCode() {
        if (anonymous) {
            return super.hashCode();
        }
        return Objects.hashCode(name);
    }

    @Override
    public int compareTo(ElSymbol o) {
        if (o == null)
            return 1;
        return name.compareTo(o.name);
    }

    public String getName() {
        return name;
    }

    public boolean isCompound() {
        return isCompound;
    }

    @Override
    @ExportMessage
    public Object toDisplayString(boolean allowSideEffects) {
        if (garbage) {
            return "|" + name + "|";
        }
        return name;
    }

    @Override
    public ElNodeMetaInfo getMetaInfo() {
        return metaInfo;
    }

    @Override
    public void setMetaInfo(ElNodeMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }

    @Override
    public String toString() {
        return (String) toDisplayString(false);
    }

}
