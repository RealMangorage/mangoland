package org.mangorage.mangoland.script.datatypes;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.script.ScriptDataTypes;

public final class VariableType implements DataType<byte[]> {
    private final ByteArrayKey internalId;

    public VariableType(final ByteArrayKey internalId) {
        this.internalId = internalId;
    }

    @Override
    public ByteArrayKey getInternalId() {
        return internalId;
    }


    @Override
    public byte[] asObject(final byte[] data) {
        return data;
    }

    @Override
    public byte[] cast(final DataType<?> from, final byte[] data) {
        return data;
    }

    @Override
    public byte[] compile(final String code) {
        return code.getBytes();
    }
}
