package org.mangorage.mangoland.engine.api;

import org.mangorage.mangoland.engine.internal.ByteArrayKeyImpl;

public sealed interface ByteArrayKey permits ByteArrayKeyImpl {
    static ByteArrayKey of(byte[] id) {
        return new ByteArrayKeyImpl(id);
    }

    byte[] get();
    boolean equals(ByteArrayKey byteArrayKey);
    boolean equals(byte[] array);
}