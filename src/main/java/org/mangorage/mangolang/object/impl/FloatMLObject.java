package org.mangorage.mangolang.object.impl;

import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.OperationType;

public final class FloatMLObject implements MangolangObject {
    private final float value;

    public FloatMLObject(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    @Override
    public MangolangObject operator(MangolangObject mangolangObject, OperationType type) {
        return switch (type) {
            case ADD -> mangolangObject.isNumeric()
                    ? applyNumericOperation(mangolangObject, type)
                    : StringMLObject.concatenate(this, mangolangObject);
            case SUBTRACT, MULTIPLY, DIVIDE, GREATER_THAN -> applyNumericOperation(mangolangObject, type);
            case EQUALS -> mangolangObject.isNumeric()
                    ? applyNumericOperation(mangolangObject, type)
                    : BooleanMLObject.FALSE;
        };
    }

    @Override
    public MangolangObject asString() {
        return new StringMLObject(Float.toString(value));
    }

    @Override
    public String toDisplayString() {
        return Float.toString(value);
    }

    @Override
    public String typeName() {
        return "float";
    }

    @Override
    public boolean isNumeric() {
        return true;
    }

    @Override
    public int numericRank() {
        return 2;
    }

    @Override
    public int asIntValue() {
        return (int) value;
    }

    @Override
    public long asLongValue() {
        return (long) value;
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
        return Float.toString(value);
    }
}

