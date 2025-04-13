package org.mangorage.mangoland.core.misc;

import java.util.Arrays;

public final class ByteArrayKey {
    public static ByteArrayKey of(byte[] id) {
        return new ByteArrayKey(id);
    }

    private final byte[] data;

    ByteArrayKey(byte[] data) {
        this.data = data;
    }

    public byte[] get() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ByteArrayKey)) return false;
        return Arrays.equals(this.data, ((ByteArrayKey) obj).data);
    }

    public boolean equals(ByteArrayKey byteArrayKey) {
        return Arrays.equals(this.data, byteArrayKey.data);
    }

    public boolean equals(byte[] array) {
        return Arrays.equals(this.data, array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }
}