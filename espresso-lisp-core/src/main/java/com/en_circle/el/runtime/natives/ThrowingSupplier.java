package com.en_circle.el.runtime.natives;

public interface ThrowingSupplier<T> {

    T get() throws Exception;

}
