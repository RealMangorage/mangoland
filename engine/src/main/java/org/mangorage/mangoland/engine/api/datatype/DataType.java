package org.mangorage.mangoland.engine.api.datatype;

import org.mangorage.mangoland.engine.api.ByteArrayKey;

import java.util.function.Function;

// Can be an integer
public interface DataType<T> {
    static <T> DataType<T> of(final byte[] key, final Function<ByteArrayKey, DataType<T>> function) {
        return of(ByteArrayKey.of(key), function);
    }

    static <T> DataType<T> of(final ByteArrayKey key, final Function<ByteArrayKey, DataType<T>> function) {
        return function.apply(key);
    }

    ByteArrayKey getDataType();

    T asObject(byte[] data);

    byte[] cast(DataType<?> from, byte[] data);
}
