package org.mangorage.mangolang.object.impl;

import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjects;

import java.nio.charset.StandardCharsets;
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
            return BooleanMLObject.of(this.value.equals(other.value));
        }
        return BooleanMLObject.FALSE;
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
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        MangolangObjects.emitHeader(out, MangolangObjects.TAG_STRING, bytes.length);
        for (byte aByte : bytes) {
            out.add(aByte);
        }
    }
}



