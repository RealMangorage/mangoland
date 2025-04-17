package org.mangorage.mangoland.engine.internal.variable;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.api.variable.Variable;
import org.mangorage.mangoland.engine.constants.CommonFlags;
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> T asObject() {
        return (T) type.asObject(data);
    }

    @Override
    public byte[] getData(final int flags) {
        if ((flags & (CommonFlags.includeDataType | CommonFlags.includeData)) == (CommonFlags.includeDataType | CommonFlags.includeData))
            return ByteUtil.merge(
                    getDataType().getInternalId().get(),
                    data
            );

        if ((flags & CommonFlags.includeData) == (CommonFlags.includeData))
            return data;

        return new byte[0];
    }
}
