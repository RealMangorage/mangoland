package org.mangorage.mangoland.core.util;

import org.mangorage.mangoland.core.datatype.DataType;
import org.mangorage.mangoland.core.misc.ByteArrayKey;

public final class Parameter {
    private final DataType<?> dataType;
    private final int length;
    private final byte[] data;

    public Parameter(DataType<?> dataType, int length, byte[] data) {
        this.dataType = dataType;
        this.length = length;
        this.data = data;
    }

    public boolean is(ByteArrayKey dataType) {
        return this.dataType.getDataType().equals(dataType);
    }

    @SuppressWarnings("unchecked")
    public <T> T asObject() {
        return (T) dataType.asObject(data);
    }

    public DataType<?> getDataType() {
        return dataType;
    }

    public int getLength() {
        return length;
    }

    public byte[] getData() {
        return data;
    }
}
