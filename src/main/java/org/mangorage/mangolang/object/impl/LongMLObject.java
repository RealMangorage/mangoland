package org.mangorage.mangolang.object.impl;

import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.OperationType;

public final class LongMLObject implements MangolangObject {
    private final long value;

    public LongMLObject(long value) {
        this.value = value;
    }

    public long getValue() {
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
        return new StringMLObject(Long.toString(value));
    }

    @Override
    public String toDisplayString() {
        return Long.toString(value);
    }

    @Override
    public String typeName() {
        return "long";
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }
}


