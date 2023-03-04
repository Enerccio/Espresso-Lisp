package com.en_circle.el.runtime.natives;

import com.en_circle.el.context.ElContext;
import com.en_circle.el.context.exceptions.ElInvocationTargetException;

import java.util.function.Supplier;

public class NativeWrappers {

    public static Runnable wrap(ThrowingRunnable throwingRunnable) {
        return () -> {
            try {
                throwingRunnable.run();
            } catch (Exception e) {
                throw new ElInvocationTargetException(e);
            }
        };
    }

    public static <T> Supplier<T> wrap(ThrowingSupplier<T> throwingSupplier) {
        return () -> {
            try {
                return throwingSupplier.get();
            } catch (Exception e) {
                throw new ElInvocationTargetException(e);
            }
        };
    }

    public static Supplier<Object> wrapVoid(Runnable voidRunnable) {
        return () -> {
            voidRunnable.run();
            return ElContext.get(null).getNil();
        };
    }


}
