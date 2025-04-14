package org.mangorage.mangoland.engine.internal;

import org.mangorage.mangoland.engine.api.ByteArrayKey;

import java.util.Arrays;

public final class ByteArrayKeyImpl implements ByteArrayKey {
    private final byte[] data;

    public ByteArrayKeyImpl(byte[] data) {
        this.data = data;
    }

    public byte[] get() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ByteArrayKeyImpl)) return false;
        return Arrays.equals(this.data, ((ByteArrayKeyImpl) obj).data);
    }

    public boolean equals(ByteArrayKey byteArrayKey) {
        return Arrays.equals(this.data, byteArrayKey.get());
    }

    public boolean equals(byte[] array) {
        return Arrays.equals(this.data, array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }
}