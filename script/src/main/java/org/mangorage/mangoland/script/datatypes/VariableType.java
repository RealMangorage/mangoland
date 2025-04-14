package org.mangorage.mangoland.script.datatypes;

import org.mangorage.mangoland.engine.api.DataType;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.script.ScriptDataTypes;

public final class VariableType implements DataType<Void> {
    @Override
    public ByteArrayKey getDataType() {
        return ScriptDataTypes.VARIABLE;
    }

    @Override
    public Void asObject(byte[] data) {
        return null;
    }

    @Override
    public byte[] cast(DataType<?> from, byte[] data) {
        return data;
    }
}
