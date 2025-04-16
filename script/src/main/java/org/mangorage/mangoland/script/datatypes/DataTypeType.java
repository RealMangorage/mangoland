package org.mangorage.mangoland.script.datatypes;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.script.ScriptDataTypes;

public final class DataTypeType implements DataType<DataType<?>> {

    private final ByteArrayKey internalId;

    public DataTypeType(final ByteArrayKey internalId) {
        this.internalId = internalId;
    }

    @Override
    public ByteArrayKey getDataType() {
        return internalId;
    }

    @Override
    public DataType<?> asObject(final byte[] data) {
        return null;
    }

    @Override
    public byte[] cast(final DataType<?> from, byte[] data) {
        return new byte[0];
    }

    @Override
    public byte[] compile(final String code) {
        return new byte[0];
    }
}
