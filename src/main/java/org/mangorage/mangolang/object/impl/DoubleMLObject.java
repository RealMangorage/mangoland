package org.mangorage.mangolang.object.impl;

import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.OperationType;

public final class DoubleMLObject implements MangolangObject {
    private final double value;

    public DoubleMLObject(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public MangolangObject operator(MangolangObject mangolangObject, OperationType type) {
        return switch (type) {
            case ADD -> NumericMLObjects.isNumeric(mangolangObject)
                    ? NumericMLObjects.applyBinaryOperation(this, mangolangObject, type)
                    : StringMLObject.concatenate(this, mangolangObject);
            case SUBTRACT, MULTIPLY, DIVIDE, GREATER_THAN -> NumericMLObjects.applyBinaryOperation(this, mangolangObject, type);
            case EQUALS -> NumericMLObjects.isNumeric(mangolangObject)
                    ? NumericMLObjects.applyBinaryOperation(this, mangolangObject, type)
                    : BooleanMLObject.FALSE;
        };
    }

    @Override
    public MangolangObject asString() {
        return new StringMLObject(Double.toString(value));
    }

    @Override
    public String toDisplayString() {
        return Double.toString(value);
    }

    @Override
    public String typeName() {
        return "double";
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}

