package org.mangorage.mangolang.object.impl;

import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjects;
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
                if (mangolangObject instanceof IntegerMLObject other) {
                    yield new IntegerMLObject(this.value + other.value);
                }
                yield StringMLObject.concatenate(this, mangolangObject);
            }
            case SUBTRACT -> {
                if (mangolangObject instanceof IntegerMLObject other) {
                    yield new IntegerMLObject(this.value - other.value);
                }
                throw MangolangObjects.unsupportedOperation(this, mangolangObject, type);
            }
            case MULTIPLY -> {
                if (mangolangObject instanceof IntegerMLObject other) {
                    yield new IntegerMLObject(this.value * other.value);
                }
                throw MangolangObjects.unsupportedOperation(this, mangolangObject, type);
            }
            case DIVIDE -> {
                if (mangolangObject instanceof IntegerMLObject other) {
                    if (other.value == 0) {
                        throw new RuntimeException("Cannot divide by zero");
                    }
                    yield new IntegerMLObject(this.value / other.value);
                }
                throw MangolangObjects.unsupportedOperation(this, mangolangObject, type);
            }
            case EQUALS -> mangolangObject instanceof IntegerMLObject other
                    ? BooleanMLObject.of(this.value == other.value)
                    : BooleanMLObject.FALSE;
            case GREATER_THAN -> {
                if (mangolangObject instanceof IntegerMLObject other) {
                    yield BooleanMLObject.of(this.value > other.value);
                }
                throw MangolangObjects.unsupportedOperation(this, mangolangObject, type);
            }
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
    public String toString() {
        return Integer.toString(value);
    }
}
