package org.mangorage.mangolang.object.codec.impl;

import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.codec.MangolangObjectCodec;
import org.mangorage.mangolang.object.impl.BooleanMLObject;

public final class BooleanMLCodec implements MangolangObjectCodec<BooleanMLObject> {
    @Override
    public int tag() {
        return MangolangObjects.TAG_BOOLEAN;
    }

    @Override
    public Class<BooleanMLObject> type() {
        return BooleanMLObject.class;
    }

    @Override
    public byte[] encodePayload(BooleanMLObject value) {
        return new byte[]{(byte) (value.value() ? 1 : 0)};
    }

    @Override
    public BooleanMLObject decodePayload(byte[] payload) {
        MangolangObjects.requirePayloadSize(tag(), payload, 1);
        return BooleanMLObject.of((payload[0] & 0xFF) != 0);
    }
}

