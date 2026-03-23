package org.mangorage.mangolang.object.impl;

import org.mangorage.mangolang.object.MangolangObject;

public final class IntegerMLObject implements MangolangObject {
    private final int value;

    public IntegerMLObject(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public MangolangObject equals(MangolangObject mangolangObject) {
        if (mangolangObject instanceof IntegerMLObject other) {
            return new IntegerMLObject(this.value == other.value ? 1 : 0);
        }
        return new IntegerMLObject(0);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
