package org.mangorage.mangolang.object.impl;

import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.OperationType;

public record BooleanMLObject(boolean value) implements MangolangObject {

    public static final BooleanMLObject TRUE = new BooleanMLObject(true);
    public static final BooleanMLObject FALSE = new BooleanMLObject(false);

    public static BooleanMLObject of(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public MangolangObject operator(MangolangObject mangolangObject, OperationType type) {
        return switch (type) {
            case ADD -> MangolangObjects.concatenateAsStrings(this, mangolangObject);
            case EQUALS -> mangolangObject instanceof BooleanMLObject other ? of(this.value == other.value) : FALSE;
            default -> throw MangolangObjects.unsupportedOperation(this, mangolangObject, type);
        };
    }

    @Override
    public MangolangObject asString() {
        return new StringMLObject(Boolean.toString(value));
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
