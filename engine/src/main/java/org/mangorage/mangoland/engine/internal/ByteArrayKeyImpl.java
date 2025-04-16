package org.mangorage.mangoland.engine.internal;

import org.mangorage.mangoland.engine.api.ByteArrayKey;

import java.util.Arrays;

public final class ByteArrayKeyImpl implements ByteArrayKey {
    private final byte[] data;

    public ByteArrayKeyImpl(final byte[] data) {
        this.data = data;
    }

    public byte[] get() {
        return data;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ByteArrayKeyImpl)) return false;
        return Arrays.equals(this.data, ((ByteArrayKeyImpl) obj).data);
    }

    public boolean equals(final ByteArrayKey byteArrayKey) {
        return Arrays.equals(this.data, byteArrayKey.get());
    }

    public boolean equals(final byte[] array) {
        return Arrays.equals(this.data, array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }
}