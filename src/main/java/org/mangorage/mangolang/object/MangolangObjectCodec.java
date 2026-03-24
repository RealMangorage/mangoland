package org.mangorage.mangolang.object;

public interface MangolangObjectCodec<T extends MangolangObject> {
    int tag();

    Class<T> type();

    byte[] encodePayload(T value);

    T decodePayload(byte[] payload);
}
