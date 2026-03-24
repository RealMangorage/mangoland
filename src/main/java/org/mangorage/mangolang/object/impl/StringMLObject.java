package org.mangorage.mangolang.object.impl;

import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.OperationType;

public final class StringMLObject implements MangolangObject {
    private final String value;

    public static StringMLObject concatenate(MangolangObject left, MangolangObject right) {
        return new StringMLObject(left.toDisplayString() + right.toDisplayString());
    }

    public StringMLObject(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public MangolangObject operator(MangolangObject mangolangObject, OperationType type) {
        return switch (type) {
            case ADD -> concatenate(this, mangolangObject);
            case EQUALS -> mangolangObject instanceof StringMLObject other
                    ? BooleanMLObject.of(this.value.equals(other.value))
                    : BooleanMLObject.FALSE;
            default -> throw MangolangObjects.unsupportedOperation(this, mangolangObject, type);
        };
    }

    @Override
    public MangolangObject asString() {
        return this;
    }

    @Override
    public String toDisplayString() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}



