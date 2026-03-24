package org.mangorage.mangolang.object.impl;

import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjects;

import java.util.List;

public record BooleanMLObject(boolean value) implements MangolangObject {

    public static final BooleanMLObject TRUE = new BooleanMLObject(true);
    public static final BooleanMLObject FALSE = new BooleanMLObject(false);

    public static BooleanMLObject of(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public MangolangObject equals(MangolangObject mangolangObject) {
        return mangolangObject instanceof BooleanMLObject other ? of(this.value == other.value) : FALSE;
    }

    @Override
    public MangolangObject asString() {
        return new StringMLObject(Boolean.toString(value));
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    @Override
    public void emitBytes(List<Byte> out) {
        MangolangObjects.emitObject(out, this);
    }
}
