package org.mangorage.mangoland.core.datatype.impl;

import org.mangorage.mangoland.core.datatype.DataType;
import org.mangorage.mangoland.core.datatype.DataTypes;
import org.mangorage.mangoland.core.misc.ByteArrayKey;
import org.mangorage.mangoland.core.util.ByteUtil;

public class StringType implements DataType<String> {
    @Override
    public ByteArrayKey getDataType() {
        return DataTypes.STRING_TYPE;
    }

    @Override
    public String asObject(byte[] data) {
        return new String(
                data
        );
    }

    @Override
    public byte[] cast(DataType<?> from, byte[] data) {
        if (DataTypes.INTEGER_TYPE.equals(from.getDataType())) {
            // Convert Integer to String
            return String.valueOf(ByteUtil.bytesToInt(data)).getBytes();
        }
        return data;
    }
}
