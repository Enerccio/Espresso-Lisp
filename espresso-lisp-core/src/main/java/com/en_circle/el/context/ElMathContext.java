package com.en_circle.el.context;

import com.en_circle.el.context.exceptions.ElRuntimeException;
import com.en_circle.el.runtime.ElNumber;
import com.en_circle.el.runtime.ElPair;

import java.math.BigDecimal;

public class ElMathContext {

    private final ElContext context;

    public ElMathContext(ElContext context) {
        this.context = context;
    }

    public Object equal(Object args) {
        ElMathOperationArguments arguments = checkArguments(args);
        Object firstArg = null;
        for (Object o : ElPair.asIterator(args)) {
            if (firstArg == null) {
                firstArg = typecast(o, arguments);
            } else {
                Object b = typecast(o, arguments);
                if (!firstArg.equals(b)) {
                    return context.getNil();
                }
            }
        }
        return context.getT();
    }

    public Object plus(Object args) {
        return applyMathOperation(args, 0, 0, this::plusInteger, this::plusDouble, this::plusBigDecimal);
    }

    public Object minus(Object args) {
        return applyMathOperation(args, null, 0, this::minusInteger, this::minusDouble, this::minusBigDecimal);
    }

    private Object plusInteger(Object a, Object b) throws ArithmeticException {
        return Math.addExact((Integer) a, (Integer) b);
    }

    private Object plusDouble(Object a, Object b) throws ArithmeticException {
        return ((double) a) + ((double) b);
    }

    private Object plusBigDecimal(Object a, Object b) throws ArithmeticException {
        return new ElNumber(((ElNumber) a).get().add(((ElNumber) b).get()));
    }

    private Object minusInteger(Object a, Object b) throws ArithmeticException {
        return Math.subtractExact((Integer) a, (Integer) b);
    }

    private Object minusDouble(Object a, Object b) throws ArithmeticException {
        return ((double) a) - ((double) b);
    }

    private Object minusBigDecimal(Object a, Object b) throws ArithmeticException {
        return new ElNumber(((ElNumber) a).get().subtract(((ElNumber) b).get()));
    }

    private ElMathOperationArguments checkArguments(Object args) {
        ElMathOperationArguments arguments = ElMathOperationArguments.INTEGER;
        for (Object o : ElPair.asIterator(args)) {
            if (o instanceof Double) {
                if (arguments == ElMathOperationArguments.INTEGER) {
                    arguments = ElMathOperationArguments.DOUBLE;
                }
            } else if (o instanceof Integer) {
                // pass
            } else if (o instanceof ElNumber) {
                arguments = ElMathOperationArguments.ELNUMBER;
            } else {
                throw new ElRuntimeException("+ requires numeric arguments");
            }
        }
        return arguments;
    }

    private Object applyMathOperation(Object args, Object startValue, Object defaultValue,
                                      Operation primitive, Operation doubleOperation, Operation decimalOperation) {
        ElMathOperationArguments arguments = checkArguments(args);
        Object firstArg = startValue;
        for (Object o : ElPair.asIterator(args)) {
            if (firstArg == null) {
                firstArg = o;
            } else {
                for (;;) {
                    Object a = typecast(firstArg, arguments);
                    Object b = typecast(o, arguments);

                    switch (arguments) {
                        case INTEGER -> {
                            try {
                                firstArg = primitive.apply(a, b);
                            } catch (ArithmeticException ignored) {
                                arguments = ElMathOperationArguments.ELNUMBER;
                                continue;
                            }
                        }
                        case DOUBLE -> firstArg = doubleOperation.apply(a, b);
                        case ELNUMBER -> firstArg = decimalOperation.apply(a, b);
                    }

                    break;
                }
            }
        }
        if (firstArg == null) {
            return defaultValue;
        }
        return downcast(firstArg);
    }

    private Object typecast(Object o, ElMathOperationArguments arguments) {
        return switch (arguments) {
            default -> o;
            case INTEGER -> o;
            case DOUBLE -> {
                if (o instanceof Integer) {
                    yield ((Integer) o).doubleValue();
                } else {
                    yield o;
                }
            }
            case ELNUMBER -> {
                if (o instanceof Integer) {
                    yield new ElNumber((Integer) o);
                } else if (o instanceof Double) {
                    yield new ElNumber((Double) o);
                } else {
                    yield o;
                }
            }
        };
    }

    private Object downcast(Object firstArg) {
        if (firstArg instanceof ElNumber) {
            BigDecimal decimal = ((ElNumber) firstArg).get();
            try {
                return decimal.intValueExact();
            } catch (ArithmeticException ignored) {

            }
        }
        return firstArg;
    }

    private interface Operation {

        Object apply(Object a, Object b) throws ArithmeticException;

    }

    private enum ElMathOperationArguments {
        INTEGER, DOUBLE, ELNUMBER
    }

}
