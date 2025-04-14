package org.mangorage.mangoland.script.util;

import org.mangorage.mangoland.engine.api.DataType;
import org.mangorage.mangoland.engine.api.ByteArrayKey;

public final class Parameter {
    private final DataType<?> dataType;
    private final byte[] data;

    public Parameter(DataType<?> dataType, byte[] data) {
        this.dataType = dataType;
        this.data = data;
    }

    public boolean is(ByteArrayKey dataType) {
        return this.dataType.getDataType().equals(dataType);
    }

    @SuppressWarnings("unchecked")
    public <T> T asObject() {
        return (T) dataType.asObject(data);
    }

    public <T> T asObject(Class<T> tClass) {
        return asObject();
    }

    public DataType<?> getDataType() {
        return dataType;
    }

    public byte[] getData() {
        return data;
    }
}
