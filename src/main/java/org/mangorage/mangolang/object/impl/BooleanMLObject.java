package org.mangorage.mangolang.object.impl;

import org.mangorage.mangolang.object.MangolangObject;

import java.util.List;

public record BooleanMLObject(boolean value) implements MangolangObject {

    public static final MangolangObject TRUE = new BooleanMLObject(true);
    public static final MangolangObject FALSE = new BooleanMLObject(false);

    @Override
    public MangolangObject equals(MangolangObject mangolangObject) {
        return mangolangObject == this ? TRUE : FALSE;
    }

    @Override
    public MangolangObject asString() {
        return new StringMLObject(Boolean.toString(value));
    }

    @Override
    public void emitBytes(List<Byte> out) {

    }
}
