package org.mangorage.mangolang.object;

import org.mangorage.mangolang.object.codec.MangolangObjectCodec;
import org.mangorage.mangolang.object.codec.impl.BooleanMLCodec;
import org.mangorage.mangolang.object.codec.impl.IntegerMLCodec;
import org.mangorage.mangolang.object.codec.impl.StringMLCodec;
import org.mangorage.mangolang.object.impl.BooleanMLObject;
import org.mangorage.mangolang.object.impl.IntegerMLObject;
import org.mangorage.mangolang.object.impl.StringMLObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MangolangObjects {
    public static final int OBJECT_PREFIX = 0xFF;
    public static final int TAG_INTEGER = 1;
    public static final int TAG_STRING = 2;
    public static final int TAG_BOOLEAN = 3;

    private static final Map<Integer, MangolangObjectCodec<? extends MangolangObject>> CODECS_BY_TAG = new HashMap<>();
    private static final Map<Class<?>, MangolangObjectCodec<? extends MangolangObject>> CODECS_BY_TYPE = new HashMap<>();

    static {
        registerCodec(new IntegerMLCodec());
        registerCodec(new StringMLCodec());
        registerCodec(new BooleanMLCodec());
    }

    private MangolangObjects() {
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

    public static void emitObject(List<Byte> out, MangolangObject value) {
        if (value == null) {
            throw new RuntimeException("Cannot emit null MangolangObject");
        }

        MangolangObjectCodec<? extends MangolangObject> codec = findCodec(value);
        if (codec == null) {
            throw new RuntimeException("No object codec registered for type: " + value.getClass().getName());
        }

        emitObject(out, codec, value);
    }

    public static MangolangObject applyOperation(MangolangObject left, MangolangObject right, OperationType type) {
        if (left == null) {
            throw new RuntimeException("Cannot apply operation " + type + " to null left operand");
        }

        return left.operator(right, type);
    }

    public static BooleanMLObject booleanObject(boolean value) {
        return BooleanMLObject.of(value);
    }

    public static StringMLObject concatenateAsStrings(MangolangObject left, MangolangObject right) {
        return new StringMLObject(toDisplayString(left) + toDisplayString(right));
    }

    public static MangolangObject literalFromToken(String token) {
        if (token == null) {
            return null;
        }

        if ("true".equalsIgnoreCase(token)) {
            return BooleanMLObject.TRUE;
        }

        if ("false".equalsIgnoreCase(token)) {
            return BooleanMLObject.FALSE;
        }

        if (token.length() >= 2 && token.charAt(0) == '"' && token.charAt(token.length() - 1) == '"') {
            return new StringMLObject(token.substring(1, token.length() - 1));
        }

        try {
            return new IntegerMLObject(Integer.parseInt(token));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static void emitHeader(List<Byte> out, int tag, int payloadLength) {
        if (payloadLength < 0 || payloadLength > 0xFFFF) {
            throw new RuntimeException("Object payload is too large: " + payloadLength + " bytes");
        }

        out.add((byte) OBJECT_PREFIX);
        out.add((byte) tag);
        out.add((byte) (payloadLength & 0xFF));
        out.add((byte) ((payloadLength >> 8) & 0xFF));
    }

    public static int requireIntegerValue(MangolangObject value, String context) {
        if (value instanceof IntegerMLObject integerObject) {
            return integerObject.getValue();
        }

        throw new RuntimeException(context + " requires an integer, got " + describe(value));
    }

    public static boolean coerceBooleanValue(MangolangObject value, String context) {
        if (value instanceof BooleanMLObject booleanObject) {
            return booleanObject.value();
        }

        if (value instanceof IntegerMLObject integerObject) {
            return integerObject.getValue() != 0;
        }

        throw new RuntimeException(context + " requires a boolean-compatible value, got " + describe(value));
    }

    public static String toDisplayString(MangolangObject value) {
        if (value == null) {
            return "null";
        }

        MangolangObject stringValue = value.asString();
        if (stringValue instanceof StringMLObject stringObject) {
            return stringObject.getValue();
        }

        return String.valueOf(stringValue);
    }

    public static String describe(MangolangObject value) {
        if (value == null) {
            return "null";
        }

        return value.getClass().getSimpleName() + "(" + toDisplayString(value) + ")";
    }

    public static RuntimeException unsupportedOperation(MangolangObject left, MangolangObject right, OperationType type) {
        return new RuntimeException("Operation " + type + " is not supported for " + describe(left)
                + (right == null ? "" : " and " + describe(right)));
    }

    private static MangolangObjectCodec<? extends MangolangObject> findCodec(MangolangObject value) {
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

    @SuppressWarnings("unchecked")
    private static <T extends MangolangObject> void emitObject(List<Byte> out, MangolangObjectCodec<? extends MangolangObject> codec, MangolangObject value) {
        MangolangObjectCodec<T> typedCodec = (MangolangObjectCodec<T>) codec;
        byte[] payload = typedCodec.encodePayload(typedCodec.type().cast(value));
        emitHeader(out, typedCodec.tag(), payload.length);
        for (byte payloadByte : payload) {
            out.add(payloadByte);
        }
    }

    public static void requirePayloadSize(int tag, byte[] payload, int expectedLength) {
        if (payload.length != expectedLength) {
            throw new RuntimeException("Invalid payload size for object tag " + tag + ": expected " + expectedLength + " but got " + payload.length);
        }
    }
}
