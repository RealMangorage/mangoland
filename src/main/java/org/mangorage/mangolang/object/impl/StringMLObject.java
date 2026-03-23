package org.mangorage.mangolang.object.impl;

import org.mangorage.mangolang.object.MangolangObject;
import java.util.List;

public final class StringMLObject implements MangolangObject {
    private final String value;

    public StringMLObject(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public MangolangObject equals(MangolangObject mangolangObject) {
        if (mangolangObject instanceof StringMLObject other) {
            return new org.mangorage.mangolang.object.impl.IntegerMLObject(this.value.equals(other.value) ? 1 : 0);
        }
        return new org.mangorage.mangolang.object.impl.IntegerMLObject(0);
    }

    @Override
    public MangolangObject asString() {
        return this;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public void emitBytes(List<Byte> out) {
        // Tag 2 = string
        out.add((byte) 2);
        byte[] bytes = value.getBytes();
        int len = Math.min(bytes.length, 64);
        out.add((byte) len);
        for (int i = 0; i < len; i++) out.add(bytes[i]);
    }
}



