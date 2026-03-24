package org.mangorage.mangolang.object;

import org.mangorage.mangolang.object.codec.MangolangObjectCodec;
import org.mangorage.mangolang.object.impl.BooleanMLObject;
import org.mangorage.mangolang.object.impl.DoubleMLObject;
import org.mangorage.mangolang.object.impl.FloatMLObject;
import org.mangorage.mangolang.object.impl.IntegerMLObject;
import org.mangorage.mangolang.object.impl.LongMLObject;
import org.mangorage.mangolang.object.impl.StringMLObject;

import java.util.List;

public final class MangolangObjectCompiler {
    private MangolangObjectCompiler() {
    }

    public static MangolangObject literalFromToken(String token) {
        if (token == null) {
            return null;
        }

        token = token.trim();
        if (token.isEmpty()) {
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

        char suffix = Character.toLowerCase(token.charAt(token.length() - 1));
        String numericBody = token.substring(0, token.length() - 1);

        if (suffix == 'l') {
            try {
                return new LongMLObject(Long.parseLong(numericBody));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        if (suffix == 'f') {
            try {
                return new FloatMLObject(Float.parseFloat(numericBody));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        if (suffix == 'd') {
            try {
                return new DoubleMLObject(Double.parseDouble(numericBody));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        try {
            return new IntegerMLObject(Integer.parseInt(token));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static void emitObject(List<Byte> out, MangolangObject value) {
        if (value == null) {
            throw new RuntimeException("Cannot emit null MangolangObject");
        }

        MangolangObjectCodec<? extends MangolangObject> codec = MangolangObjects.findCodec(value);
        if (codec == null) {
            throw new RuntimeException("No object codec registered for type: " + value.getClass().getName());
        }

        emitObject(out, codec, value);
    }

    public static void emitHeader(List<Byte> out, int tag, int payloadLength) {
        if (payloadLength < 0 || payloadLength > 0xFFFF) {
            throw new RuntimeException("Object payload is too large: " + payloadLength + " bytes");
        }

        out.add((byte) MangolangObjects.OBJECT_PREFIX);
        out.add((byte) tag);
        out.add((byte) (payloadLength & 0xFF));
        out.add((byte) ((payloadLength >> 8) & 0xFF));
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
}

