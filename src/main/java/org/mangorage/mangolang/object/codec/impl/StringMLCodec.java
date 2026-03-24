package org.mangorage.mangolang.object.codec.impl;

import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.codec.MangolangObjectCodec;
import org.mangorage.mangolang.object.impl.StringMLObject;

import java.nio.charset.StandardCharsets;

public final class StringMLCodec implements MangolangObjectCodec<StringMLObject> {
    @Override
    public int tag() {
        return MangolangObjects.TAG_STRING;
    }

    @Override
    public Class<StringMLObject> type() {
        return StringMLObject.class;
    }

    @Override
    public byte[] encodePayload(StringMLObject value) {
        return value.getValue().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public StringMLObject decodePayload(byte[] payload) {
        return new StringMLObject(new String(payload, StandardCharsets.UTF_8));
    }
}

