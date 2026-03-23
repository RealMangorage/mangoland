package org.mangorage.mangolang.object.impl;

import org.mangorage.mangolang.object.MangolangObject;
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
            return this.value == other.value ? BooleanMLObject.TRUE : BooleanMLObject.FALSE;
        }
        return BooleanMLObject.FALSE;
    }

    @Override
    public MangolangObject asString() {
        return new StringMLObject(Integer.toString(value));
    }

    @Override
    public void emitBytes(List<Byte> out) {
        // Tag 1 = integer
        out.add((byte) 1);
        // Length (4 bytes)
        out.add((byte) 4);
        // Big-endian 4-byte integer
        out.add((byte) ((value >> 24) & 0xFF));
        out.add((byte) ((value >> 16) & 0xFF));
        out.add((byte) ((value >> 8) & 0xFF));
        out.add((byte) (value & 0xFF));
    }
}
