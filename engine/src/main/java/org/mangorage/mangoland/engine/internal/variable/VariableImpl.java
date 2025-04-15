package org.mangorage.mangoland.engine.internal.variable;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.api.variable.Variable;
import org.mangorage.mangoland.engine.util.ByteUtil;

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

    @Override
    public byte[] getFullData() {
        return ByteUtil.merge(type.getDataType().get(), data);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T asObject() {
        return (T) type.asObject(data);
    }
}
