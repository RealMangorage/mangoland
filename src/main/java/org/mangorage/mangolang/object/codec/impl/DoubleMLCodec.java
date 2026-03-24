package org.mangorage.mangolang.object.codec.impl;

import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.codec.MangolangObjectCodec;
import org.mangorage.mangolang.object.impl.DoubleMLObject;

public final class DoubleMLCodec implements MangolangObjectCodec<DoubleMLObject> {
    public static final int TAG = MangolangObjects.generateTag();

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public Class<DoubleMLObject> type() {
        return DoubleMLObject.class;
    }

    @Override
    public byte[] encodePayload(DoubleMLObject value) {
        long bits = Double.doubleToLongBits(value.getValue());
        return new byte[]{
                (byte) ((bits >> 56) & 0xFF),
                (byte) ((bits >> 48) & 0xFF),
                (byte) ((bits >> 40) & 0xFF),
                (byte) ((bits >> 32) & 0xFF),
                (byte) ((bits >> 24) & 0xFF),
                (byte) ((bits >> 16) & 0xFF),
                (byte) ((bits >> 8) & 0xFF),
                (byte) (bits & 0xFF)
        };
    }

    @Override
    public DoubleMLObject decodePayload(byte[] payload) {
        MangolangObjects.requirePayloadSize(tag(), payload, 8);
        long bits = ((long) (payload[0] & 0xFF) << 56)
                | ((long) (payload[1] & 0xFF) << 48)
                | ((long) (payload[2] & 0xFF) << 40)
                | ((long) (payload[3] & 0xFF) << 32)
                | ((long) (payload[4] & 0xFF) << 24)
                | ((long) (payload[5] & 0xFF) << 16)
                | ((long) (payload[6] & 0xFF) << 8)
                | (payload[7] & 0xFFL);
        return new DoubleMLObject(Double.longBitsToDouble(bits));
    }
}

