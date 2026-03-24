package org.mangorage.mangolang.object;

import org.mangorage.mangolang.object.impl.BooleanMLObject;
import org.mangorage.mangolang.object.impl.IntegerMLObject;
import org.mangorage.mangolang.object.impl.StringMLObject;

import java.util.List;

public final class MangolangObjects {
    public static final int OBJECT_PREFIX = 0xFF;
    public static final int TAG_INTEGER = 1;
    public static final int TAG_STRING = 2;
    public static final int TAG_BOOLEAN = 3;

    private MangolangObjects() {
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
}
