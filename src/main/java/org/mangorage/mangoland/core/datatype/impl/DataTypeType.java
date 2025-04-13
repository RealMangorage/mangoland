package org.mangorage.mangoland.core.datatype.impl;

import org.mangorage.mangoland.core.datatype.DataType;
import org.mangorage.mangoland.core.datatype.DataTypes;
import org.mangorage.mangoland.core.misc.ByteArrayKey;

public final class DataTypeType implements DataType<DataType<?>> {
    @Override
    public ByteArrayKey getDataType() {
        return DataTypes.DATA_TYPE;
    }

    @Override
    public DataType<?> asObject(byte[] data) {
        return null;
    }

    @Override
    public byte[] cast(DataType<?> from, byte[] data) {
        return new byte[0];
    }
}
