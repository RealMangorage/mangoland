package org.mangorage.mangoland.core.datatype;

import org.mangorage.mangoland.core.misc.ByteArrayKey;

// Can be an integer
public interface DataType<T> {
    ByteArrayKey getDataType();

    T asObject(byte[] data);

    byte[] cast(DataType<?> from, byte[] data);
}
