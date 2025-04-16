package org.mangorage.mangoland.engine.api;

import org.mangorage.mangoland.engine.internal.ByteArrayKeyImpl;

/**
    Utility Class to provide a way to use byte[] as a Key in Maps
 */
public sealed interface ByteArrayKey permits ByteArrayKeyImpl {
    static ByteArrayKey of(final byte[] id) {
        return new ByteArrayKeyImpl(id);
    }

    byte[] get();
    boolean equals(final ByteArrayKey byteArrayKey);
    boolean equals(final byte[] array);
}