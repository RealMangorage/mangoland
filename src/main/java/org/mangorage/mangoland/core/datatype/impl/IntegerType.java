package org.mangorage.mangoland.core.datatype.impl;

import org.mangorage.mangoland.core.datatype.DataType;
import org.mangorage.mangoland.core.datatype.DataTypes;
import org.mangorage.mangoland.core.misc.ByteArrayKey;
import org.mangorage.mangoland.core.util.ByteUtil;

public final class IntegerType implements DataType<Integer> {

    @Override
    public ByteArrayKey getDataType() {
        return DataTypes.INTEGER_TYPE;
    }

    @Override
    public Integer asObject(byte[] data) {
        return ByteUtil.bytesToInt(data);
    }

    @Override
    public byte[] cast(DataType<?> from, byte[] data) {
        if (DataTypes.STRING_TYPE.equals(from.getDataType())) {
            // Convert Integer to String
            return ByteUtil.intToBytesLE(
                    Integer.parseInt(
                            new String(data)
                    )
            );
        }
        return data;
    }

}
