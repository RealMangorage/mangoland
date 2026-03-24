package org.mangorage.mangolang.object.impl;

import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.OperationType;

public final class IntegerMLObject implements MangolangObject {
    private final int value;

    public static int requireIntegerValue(MangolangObject value, String context) {
        if (value instanceof IntegerMLObject integerObject) {
            return integerObject.getValue();
        }

        throw new RuntimeException(context + " requires an integer, got " + (value == null ? "null" : value.describe()));
    }

    public static boolean coerceBooleanValue(IntegerMLObject value) {
        return value.getValue() != 0;
    }

    public IntegerMLObject(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public MangolangObject operator(MangolangObject mangolangObject, OperationType type) {
        return switch (type) {
            case ADD -> {
                if (mangolangObject.isNumeric()) {
                    yield applyNumericOperation(mangolangObject, type);
                }
                yield StringMLObject.concatenate(this, mangolangObject);
            }
            case SUBTRACT, MULTIPLY, DIVIDE, GREATER_THAN -> applyNumericOperation(mangolangObject, type);
            case EQUALS -> mangolangObject.isNumeric()
                    ? applyNumericOperation(mangolangObject, type)
                    : BooleanMLObject.FALSE;
        };
    }

    @Override
    public MangolangObject asString() {
        return new StringMLObject(Integer.toString(value));
    }

    @Override
    public String toDisplayString() {
        return Integer.toString(value);
    }

    @Override
    public String typeName() {
        return "integer";
    }

    @Override
    public boolean isNumeric() {
        return true;
    }

    @Override
    public int numericRank() {
        return 0;
    }

    @Override
    public int asIntValue() {
        return value;
    }

    @Override
    public long asLongValue() {
        return value;
    }

    @Override
    public float asFloatValue() {
        return value;
    }

    @Override
    public double asDoubleValue() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
