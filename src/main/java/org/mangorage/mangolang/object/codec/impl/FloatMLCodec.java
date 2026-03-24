package org.mangorage.mangolang.object.codec.impl;

import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.codec.MangolangObjectCodec;
import org.mangorage.mangolang.object.impl.FloatMLObject;

public final class FloatMLCodec implements MangolangObjectCodec<FloatMLObject> {
    public static final int TAG = MangolangObjects.generateTag();

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public Class<FloatMLObject> type() {
        return FloatMLObject.class;
    }

    @Override
    public byte[] encodePayload(FloatMLObject value) {
        int bits = Float.floatToIntBits(value.getValue());
        return new byte[]{
                (byte) ((bits >> 24) & 0xFF),
                (byte) ((bits >> 16) & 0xFF),
                (byte) ((bits >> 8) & 0xFF),
                (byte) (bits & 0xFF)
        };
    }

    @Override
    public FloatMLObject decodePayload(byte[] payload) {
        MangolangObjects.requirePayloadSize(tag(), payload, 4);
        int bits = ((payload[0] & 0xFF) << 24)
                | ((payload[1] & 0xFF) << 16)
                | ((payload[2] & 0xFF) << 8)
                | (payload[3] & 0xFF);
        return new FloatMLObject(Float.intBitsToFloat(bits));
    }
}

