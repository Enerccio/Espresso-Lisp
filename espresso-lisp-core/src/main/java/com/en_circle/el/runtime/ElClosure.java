package com.en_circle.el.runtime;

import com.en_circle.el.ElLanguage;
import com.en_circle.el.context.ElSymbolHelper;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;

@ExportLibrary(InteropLibrary.class)
public class ElClosure extends DynamicObject implements ElObject {

    private final ElClosure parentClosure;

    public ElClosure(Shape shape, ElClosure parentClosure) {
        super(shape);
        this.parentClosure = parentClosure;
    }

    @ExportMessage
    boolean hasMembers(@CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        return objectLibrary.getKeyArray(this).length > 0;
    }

    @ExportMessage
    boolean isMemberReadable(String member, @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        return isMemberReadableInt(ElSymbolHelper.getSymbol(member), objectLibrary);
    }

    public boolean hasBinding(ElSymbol name) {
        return isMemberReadableInt(name, DynamicObjectLibrary.getUncached());
    }

    public boolean isMemberReadableInt(ElSymbol member, @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        return objectLibrary.containsKey(this, member);
    }

    @ExportMessage
    Object readMember(String member,
                             @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        return readMemberInt(ElSymbolHelper.getSymbol(member), objectLibrary);
    }

    public Object getBinding(ElSymbol name) {
        return readMemberInt(name, DynamicObjectLibrary.getUncached());
    }

    public Object readMemberInt(ElSymbol member,
                             @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        return objectLibrary.getOrDefault(this, member, null);
    }

    public void setMember(ElSymbol member,
                          @CachedLibrary("this") DynamicObjectLibrary objectLibrary, Object value) {
        objectLibrary.put(this, member, value);
    }

    void setMember(String member,
                          @CachedLibrary("this") DynamicObjectLibrary objectLibrary, Object value) {
        setMember(ElSymbolHelper.getSymbol(member), objectLibrary, value);
    }

    public void setBinding(ElSymbol name, Object value) {
        setMember(name, DynamicObjectLibrary.getUncached(), value);
    }

    @ExportMessage
    final Object getMembers(boolean includeInternal) throws UnsupportedMessageException {
        return null;
    }

    public ElClosure getParentClosure() {
        return parentClosure;
    }

    @ExportMessage
    boolean isScope() {
        return true;
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @ExportMessage
    final <T> Class<? extends TruffleLanguage<T>> getLanguage() throws UnsupportedMessageException {
        return (Class<? extends TruffleLanguage<T>>) (Object) ElLanguage.class;
    }

    @Override
    @ExportMessage
    public Object toDisplayString(boolean allowSideEffects) {
        return toString();
    }

}
