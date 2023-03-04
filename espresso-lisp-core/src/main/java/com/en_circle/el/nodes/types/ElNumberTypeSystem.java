package com.en_circle.el.nodes.types;

import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;
import com.oracle.truffle.api.dsl.TypeSystem;

@TypeSystem(
        {
                int.class,
                double.class
        }
)
public class ElNumberTypeSystem {


    @TypeCheck(double.class)
    public static boolean isDouble(Object value) {
        return value instanceof Double || value instanceof Integer;
    }

    @TypeCast(double.class)
    public static double asDouble(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else {
            return (double) value;
        }
    }

}
