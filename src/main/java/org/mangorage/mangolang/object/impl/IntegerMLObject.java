package org.mangorage.mangolang.object.impl;

import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjects;

import java.util.List;

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
            return BooleanMLObject.of(this.value == other.value);
        }
        return BooleanMLObject.FALSE;
    }

    @Override
    public MangolangObject asString() {
        return new StringMLObject(Integer.toString(value));
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public void emitBytes(List<Byte> out) {
        MangolangObjects.emitHeader(out, MangolangObjects.TAG_INTEGER, 4);
        out.add((byte) ((value >> 24) & 0xFF));
        out.add((byte) ((value >> 16) & 0xFF));
        out.add((byte) ((value >> 8) & 0xFF));
        out.add((byte) (value & 0xFF));
    }
}
