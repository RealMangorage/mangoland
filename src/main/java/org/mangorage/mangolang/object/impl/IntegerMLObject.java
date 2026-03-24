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
                if (NumericMLObjects.isNumeric(mangolangObject)) {
                    yield NumericMLObjects.applyBinaryOperation(this, mangolangObject, type);
                }
                yield StringMLObject.concatenate(this, mangolangObject);
            }
            case SUBTRACT, MULTIPLY, DIVIDE, GREATER_THAN -> NumericMLObjects.applyBinaryOperation(this, mangolangObject, type);
            case EQUALS -> NumericMLObjects.isNumeric(mangolangObject)
                    ? NumericMLObjects.applyBinaryOperation(this, mangolangObject, type)
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
    public String toString() {
        return Integer.toString(value);
    }
}
