package org.mangorage.mangolang.object.codec.impl;

import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.codec.MangolangObjectCodec;
import org.mangorage.mangolang.object.impl.IntegerMLObject;

public final class IntegerMLCodec implements MangolangObjectCodec<IntegerMLObject> {
    public static final int TAG = MangolangObjects.generateTag();

    public static String toDisplayString(IntegerMLObject value) {
        return Integer.toString(value.getValue());
    }

    public static int requireIntegerValue(IntegerMLObject value) {
        return value.getValue();
    }

    public static boolean coerceBooleanValue(IntegerMLObject value) {
        return value.getValue() != 0;
    }

    @Override
    public int tag() {
        return TAG;
    }

    @Override
    public Class<IntegerMLObject> type() {
        return IntegerMLObject.class;
    }

    @Override
    public byte[] encodePayload(IntegerMLObject value) {
        int intValue = value.getValue();
        return new byte[]{
                (byte) ((intValue >> 24) & 0xFF),
                (byte) ((intValue >> 16) & 0xFF),
                (byte) ((intValue >> 8) & 0xFF),
                (byte) (intValue & 0xFF)
        };
    }

    @Override
    public IntegerMLObject decodePayload(byte[] payload) {
        MangolangObjects.requirePayloadSize(tag(), payload, 4);
        int value = ((payload[0] & 0xFF) << 24)
                | ((payload[1] & 0xFF) << 16)
                | ((payload[2] & 0xFF) << 8)
                | (payload[3] & 0xFF);
        return new IntegerMLObject(value);
    }
}

