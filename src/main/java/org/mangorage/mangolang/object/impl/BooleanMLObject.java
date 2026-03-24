package org.mangorage.mangolang.object.impl;

import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.OperationType;

public record BooleanMLObject(boolean value) implements MangolangObject {

    public static final BooleanMLObject TRUE = new BooleanMLObject(true);
    public static final BooleanMLObject FALSE = new BooleanMLObject(false);

    public static boolean coerceBooleanValue(MangolangObject value, String context) {
        if (value instanceof BooleanMLObject booleanObject) {
            return booleanObject.value();
        }

        if (value instanceof IntegerMLObject integerObject) {
            return IntegerMLObject.coerceBooleanValue(integerObject);
        }

        throw new RuntimeException(context + " requires a boolean-compatible value, got " + (value == null ? "null" : value.describe()));
    }

    public static BooleanMLObject of(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public MangolangObject operator(MangolangObject mangolangObject, OperationType type) {
        return switch (type) {
            case ADD -> StringMLObject.concatenate(this, mangolangObject);
            case EQUALS -> mangolangObject instanceof BooleanMLObject other ? of(this.value == other.value) : FALSE;
            default -> throw MangolangObjects.unsupportedOperation(this, mangolangObject, type);
        };
    }

    @Override
    public MangolangObject asString() {
        return new StringMLObject(Boolean.toString(value));
    }

    @Override
    public String toDisplayString() {
        return Boolean.toString(value);
    }

    @Override
    public String typeName() {
        return "boolean";
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
