package org.mangorage.mangolang.object.impl;

import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.OperationType;

public final class StringMLObject implements MangolangObject {
    private final String value;

    public StringMLObject(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public MangolangObject operator(MangolangObject mangolangObject, OperationType type) {
        return switch (type) {
            case ADD -> MangolangObjects.concatenateAsStrings(this, mangolangObject);
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
    public String toString() {
        return value;
    }
}



