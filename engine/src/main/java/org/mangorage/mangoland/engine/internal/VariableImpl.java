package org.mangorage.mangoland.engine.internal;

import org.mangorage.mangoland.engine.api.DataType;
import org.mangorage.mangoland.engine.api.Variable;

public final class VariableImpl implements Variable {
    private final DataType<?> type;
    private final byte[] data;

    public VariableImpl(final DataType<?> dataType, final byte[] data) {
        this.type = dataType;
        this.data = data;
    }

    @Override
    public DataType<?> getDataType() {
        return type;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T asObject() {
        return (T) type.asObject(data);
    }
}
