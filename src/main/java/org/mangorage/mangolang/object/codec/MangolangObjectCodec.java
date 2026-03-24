package org.mangorage.mangolang.object.codec;

import org.mangorage.mangolang.object.MangolangObject;

public interface MangolangObjectCodec<T extends MangolangObject> {
    int tag();

    Class<T> type();

    byte[] encodePayload(T value);

    T decodePayload(byte[] payload);
}
