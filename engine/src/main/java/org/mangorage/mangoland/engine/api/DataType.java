package org.mangorage.mangoland.engine.api;

// Can be an integer
public interface DataType<T> {
    ByteArrayKey getDataType();

    T asObject(byte[] data);

    byte[] cast(DataType<?> from, byte[] data);
}
