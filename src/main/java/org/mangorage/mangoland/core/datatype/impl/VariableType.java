package org.mangorage.mangoland.core.datatype.impl;

import org.mangorage.mangoland.core.datatype.DataType;
import org.mangorage.mangoland.core.datatype.DataTypes;
import org.mangorage.mangoland.core.misc.ByteArrayKey;

public final class VariableType implements DataType<Void> {
    @Override
    public ByteArrayKey getDataType() {
        return DataTypes.VARIABLE;
    }

    @Override
    public Void asObject(byte[] data) {
        return null;
    }
}
