package com.en_circle.el.runtime;

import java.math.BigDecimal;

public class ElNumber implements ElObject {

    private final BigDecimal decimal;

    public ElNumber(Integer number) {
        this.decimal = new BigDecimal(number);
    }

    public ElNumber(Double number) {
        this.decimal = new BigDecimal(number);
    }

    public ElNumber(BigDecimal number) {
        this.decimal = number;
    }

    @Override
    public Object toDisplayString(boolean allowSideEffects) {
        return decimal.toPlainString();
    }

    @Override
    public String toString() {
        return (String) toDisplayString(true);
    }

    public BigDecimal get() {
        return decimal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ElNumber elNumber = (ElNumber) o;

        return decimal.equals(elNumber.decimal);
    }

    @Override
    public int hashCode() {
        return decimal.hashCode();
    }
}
