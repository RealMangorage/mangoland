package org.mangorage.mangoland.script.datatypes;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.script.ScriptDataTypes;

public final class VariableType implements DataType<Void> {
    private final ByteArrayKey internalId;

    public VariableType(final ByteArrayKey internalId) {
        this.internalId = internalId;
    }

    @Override
    public ByteArrayKey getDataType() {
        return internalId;
    }


    @Override
    public Void asObject(final byte[] data) {
        return null;
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
