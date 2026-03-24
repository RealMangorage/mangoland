package org.mangorage.mangolang.object.codec.impl;

import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.codec.MangolangObjectCodec;
import org.mangorage.mangolang.object.impl.LongMLObject;

public final class LongMLCodec implements MangolangObjectCodec<LongMLObject> {
    public static final int TAG = MangolangObjects.generateTag();

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public Class<LongMLObject> type() {
        return LongMLObject.class;
    }

    @Override
    public byte[] encodePayload(LongMLObject value) {
        long longValue = value.getValue();
        return new byte[]{
                (byte) ((longValue >> 56) & 0xFF),
                (byte) ((longValue >> 48) & 0xFF),
                (byte) ((longValue >> 40) & 0xFF),
                (byte) ((longValue >> 32) & 0xFF),
                (byte) ((longValue >> 24) & 0xFF),
                (byte) ((longValue >> 16) & 0xFF),
                (byte) ((longValue >> 8) & 0xFF),
                (byte) (longValue & 0xFF)
        };
    }

    @Override
    public LongMLObject decodePayload(byte[] payload) {
        MangolangObjects.requirePayloadSize(tag(), payload, 8);
        long value = ((long) (payload[0] & 0xFF) << 56)
                | ((long) (payload[1] & 0xFF) << 48)
                | ((long) (payload[2] & 0xFF) << 40)
                | ((long) (payload[3] & 0xFF) << 32)
                | ((long) (payload[4] & 0xFF) << 24)
                | ((long) (payload[5] & 0xFF) << 16)
                | ((long) (payload[6] & 0xFF) << 8)
                | (payload[7] & 0xFFL);
        return new LongMLObject(value);
    }
}

