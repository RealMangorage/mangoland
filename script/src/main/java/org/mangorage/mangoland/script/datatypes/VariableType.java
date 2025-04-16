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
    public Void asObject(byte[] data) {
        return null;
    }

    @Override
    public byte[] cast(DataType<?> from, byte[] data) {
        return data;
    }

    @Override
    public byte[] compile(String code) {
        return code.getBytes();
    }
}
