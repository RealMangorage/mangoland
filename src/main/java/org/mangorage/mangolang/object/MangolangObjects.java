package org.mangorage.mangolang.object;

import org.mangorage.mangolang.object.impl.BooleanMLObject;
import org.mangorage.mangolang.object.impl.IntegerMLObject;
import org.mangorage.mangolang.object.impl.StringMLObject;

import java.nio.charset.StandardCharsets;
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
        registerCodec(new MangolangObjectCodec<IntegerMLObject>() {
            @Override
            public int tag() {
                return TAG_INTEGER;
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
                requirePayloadSize(TAG_INTEGER, payload, 4);
                int val = ((payload[0] & 0xFF) << 24)
                        | ((payload[1] & 0xFF) << 16)
                        | ((payload[2] & 0xFF) << 8)
                        | (payload[3] & 0xFF);
                return new IntegerMLObject(val);
            }
        });

        registerCodec(new MangolangObjectCodec<StringMLObject>() {
            @Override
            public int tag() {
                return TAG_STRING;
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
        });

        registerCodec(new MangolangObjectCodec<BooleanMLObject>() {
            @Override
            public int tag() {
                return TAG_BOOLEAN;
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
                requirePayloadSize(TAG_BOOLEAN, payload, 1);
                return BooleanMLObject.of((payload[0] & 0xFF) != 0);
            }
        });
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

    public static BooleanMLObject booleanObject(boolean value) {
        return BooleanMLObject.of(value);
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

    private static void requirePayloadSize(int tag, byte[] payload, int expectedLength) {
        if (payload.length != expectedLength) {
            throw new RuntimeException("Invalid payload size for object tag " + tag + ": expected " + expectedLength + " but got " + payload.length);
        }
    }
}
