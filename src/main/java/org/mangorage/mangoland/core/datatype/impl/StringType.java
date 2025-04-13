package org.mangorage.mangoland.core.datatype.impl;

import org.mangorage.mangoland.core.datatype.DataType;
import org.mangorage.mangoland.core.datatype.DataTypes;
import org.mangorage.mangoland.core.misc.ByteArrayKey;

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
}
