package org.mangorage.mangoland.engine.api;

import org.mangorage.mangoland.engine.internal.ByteArrayKeyImpl;

/**
    Utility Class to provide a way to use byte[] as a Key in Maps
 */
public sealed interface ByteArrayKey permits ByteArrayKeyImpl {
    static ByteArrayKey of(byte[] id) {
        return new ByteArrayKeyImpl(id);
    }

    byte[] get();
    boolean equals(ByteArrayKey byteArrayKey);
    boolean equals(byte[] array);
}