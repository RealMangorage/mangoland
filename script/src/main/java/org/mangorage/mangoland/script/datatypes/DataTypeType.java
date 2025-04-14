package org.mangorage.mangoland.script.datatypes;

import org.mangorage.mangoland.engine.api.DataType;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.script.ScriptDataTypes;

public final class DataTypeType implements DataType<DataType<?>> {
    @Override
    public ByteArrayKey getDataType() {
        return ScriptDataTypes.DATA_TYPE;
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
