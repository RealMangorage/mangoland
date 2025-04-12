package org.mangorage.mangoland.data;

import java.util.Arrays;

public final class ByteArrayKey {
    public static ByteArrayKey of(byte[] id) {
        return new ByteArrayKey(id);
    }

    private final byte[] data;

    ByteArrayKey(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ByteArrayKey)) return false;
        return Arrays.equals(this.data, ((ByteArrayKey) obj).data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }
}