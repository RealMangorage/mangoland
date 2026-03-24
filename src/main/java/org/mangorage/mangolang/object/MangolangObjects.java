package org.mangorage.mangolang.object;

import org.mangorage.mangolang.object.codec.MangolangObjectCodec;
import org.mangorage.mangolang.object.codec.impl.BooleanMLCodec;
import org.mangorage.mangolang.object.codec.impl.IntegerMLCodec;
import org.mangorage.mangolang.object.codec.impl.StringMLCodec;

import java.util.HashMap;
import java.util.Map;

public final class MangolangObjects {
    public static final int OBJECT_PREFIX = 0xFF;

    private static final Map<Integer, MangolangObjectCodec<? extends MangolangObject>> CODECS_BY_TAG = new HashMap<>();
    private static final Map<Class<?>, MangolangObjectCodec<? extends MangolangObject>> CODECS_BY_TYPE = new HashMap<>();
    private static int nextGeneratedTag = 1;

    static {
        registerCodec(new IntegerMLCodec());
        registerCodec(new StringMLCodec());
        registerCodec(new BooleanMLCodec());
    }

    private MangolangObjects() {
    }

    public static synchronized int generateTag() {
        if (nextGeneratedTag > 0xFF) {
            throw new RuntimeException("No object tags remaining");
        }

        return nextGeneratedTag++;
    }

    public static <T extends MangolangObject> void registerCodec(MangolangObjectCodec<T> codec) {
        MangolangObjectCodec<? extends MangolangObject> existingTagCodec = CODECS_BY_TAG.putIfAbsent(codec.tag(), codec);
        if (existingTagCodec != null) {
            throw new RuntimeException("Object tag already registered: " + codec.tag());
        }

        MangolangObjectCodec<? extends MangolangObject> existingTypeCodec = CODECS_BY_TYPE.putIfAbsent(codec.type(), codec);
        if (existingTypeCodec != null) {
            throw new RuntimeException("Object codec already registered for type: " + codec.type().getName());
        }
    }

    public static MangolangObject decode(int tag, byte[] payload) {
        MangolangObjectCodec<? extends MangolangObject> codec = CODECS_BY_TAG.get(tag);
        if (codec == null) {
            throw new RuntimeException("Unknown object tag: " + tag);
        }

        return codec.decodePayload(payload);
    }

    public static MangolangObject applyOperation(MangolangObject left, MangolangObject right, OperationType type) {
        if (left == null) {
            throw new RuntimeException("Cannot apply operation " + type + " to null left operand");
        }

        return left.operator(right, type);
    }

    public static RuntimeException unsupportedOperation(MangolangObject left, MangolangObject right, OperationType type) {
        return new RuntimeException("Operation " + type + " is not supported for "
                + (left == null ? "null" : left.describe())
                + (right == null ? "" : " and " + right.describe()));
    }

    static MangolangObjectCodec<? extends MangolangObject> findCodec(MangolangObject value) {
        MangolangObjectCodec<? extends MangolangObject> directCodec = CODECS_BY_TYPE.get(value.getClass());
        if (directCodec != null) {
            return directCodec;
        }

        for (MangolangObjectCodec<? extends MangolangObject> codec : CODECS_BY_TYPE.values()) {
            if (codec.type().isInstance(value)) {
                return codec;
            }
        }

        return null;
    }


    public static void requirePayloadSize(int tag, byte[] payload, int expectedLength) {
        if (payload.length != expectedLength) {
            throw new RuntimeException("Invalid payload size for object tag " + tag + ": expected " + expectedLength + " but got " + payload.length);
        }
    }
}
