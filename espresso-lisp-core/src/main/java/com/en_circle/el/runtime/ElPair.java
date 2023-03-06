package com.en_circle.el.runtime;

import com.en_circle.el.context.ElContext;
import com.en_circle.el.context.exceptions.ElRuntimeException;
import com.en_circle.el.nodes.ElNodeMetaInfo;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

import java.util.*;

@ExportLibrary(InteropLibrary.class)
public class ElPair implements ElObject, ElHasSourceInfo {
    private static final ThreadLocal<Set<Object>> PRINT_GUARD = ThreadLocal.withInitial(HashSet::new);

    private Object a;
    private Object b;
    private ElNodeMetaInfo metaInfo;

    public ElPair(Object a) {
        this(a, ElContext.get(null).getNil());
    }

    public ElPair(Object a, Object b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        this.a = a;
        this.b = b;
    }

    private Object carImpl() {
        return a;
    }

    private Object cdrImpl() {
        return b;
    }

    @Override
    public String toString() {
        return (String) toDisplayString(false);
    }

    @Override
    @ExportMessage
    public Object toDisplayString(boolean allowSideEffects) {
        return printList(allowSideEffects, false);
    }

    private String printList(boolean allowSideEffects, boolean isContinue) {
        if (PRINT_GUARD.get().contains(this)) {
            return "...";
        }
        try {
            PRINT_GUARD.get().add(this);

            StringBuilder builder = new StringBuilder();
            if (!isContinue)
                builder.append("(");

            if (a instanceof ElObject) {
                builder.append(((ElObject) a).toDisplayString(allowSideEffects));
            } else {
                builder.append(a);
            }
            if (b == ElContext.get(null).getNil()) {
                builder.append(")");
            } else if (b instanceof ElPair list) {
                builder.append(" ");
                builder.append(list.printList(allowSideEffects, true));
            } else {
                builder.append(" . ");
                if (b instanceof ElObject) {
                    builder.append(((ElObject) b).toDisplayString(allowSideEffects));
                } else {
                    builder.append(b);
                }
            }
            return builder.toString();
        } finally {
            PRINT_GUARD.get().remove(this);
        }
    }

    @Override
    public ElNodeMetaInfo getMetaInfo() {
        return metaInfo;
    }

    @Override
    public void setMetaInfo(ElNodeMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }

    public static int size(Object pair) {
        ElSymbol nil = ElContext.get(null).getNil();
        int size = 0;
        while (pair != null) {
            size++;
            if (pair instanceof ElPair p) {
                pair = p.b;
            } else if (pair == nil) {
                break;
            } else {
                ++size;
                break;
            }
        }
        return size;
    }

    public static boolean empty(Object pair) {
        return !(pair instanceof ElPair);
    }

    public static Object car(Object pair) {
        if (pair instanceof ElPair p)
            return p.carImpl();
        return ElContext.get(null).getNil();
    }

    public static Object cdr(Object pair) {
        if (pair instanceof ElPair p)
            return p.cdrImpl();
        return ElContext.get(null).getNil();
    }

    public static Object nth(Object pair, int ix) {
        while (ix > 0) {
            --ix;
            if (pair instanceof ElPair p)
                pair = p.b;
        }
        if (pair instanceof ElPair p)
            return p.a;
        return ElContext.get(null).getNil();
    }

    public static Iterable<Object> asIterator(Object list) {
        if (list instanceof ElPair pair)
            return new PairIterable(pair);
        return Collections.singleton(ElContext.get(null).getNil());
    }

    public static Iterable<Object> asStrictIterator(Object list) {
        Object nil = ElContext.get(null).getNil();
        if (list == nil)
            return Collections.singleton(nil);
        if (list instanceof ElPair pair)
            return new PairIterable(pair);
        throw new ElRuntimeException("Object " + list + " is not list/nil");
    }

    private record PairIterable(ElPair head) implements Iterable<Object> {

        @Override
            public Iterator<Object> iterator() {
                return new PairIterator(head);
            }
        }

    private static class PairIterator implements Iterator<Object> {

        private Object head;

        private PairIterator(Object head) {
            this.head = head;
        }

        @Override
        public boolean hasNext() {
            return (head instanceof ElPair);
        }

        @Override
        public Object next() {
            Object v = ElPair.car(head);
            head = ElPair.cdr(head);
            return v;
        }
    }

    public static Object fromList(List<Object> elements) {
        if (elements == null || elements.size() == 0)
            return ElContext.get(null).getNil();

        ElPair head = new ElPair(elements.get(0));
        ElPair current = head;
        for (int i=1; i<elements.size(); i++) {
            ElPair newPair = new ElPair(elements.get(i));
            current.b = newPair;
            current = newPair;
        }
        return head;
    }
}
