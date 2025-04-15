package org.mangorage.mangoland.engine.api.variable;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.internal.variable.VariableImpl;

public sealed interface Variable permits VariableImpl {
    static Variable of(final DataType<?> dataType, final byte[] data) {
        return new VariableImpl(dataType, data);
    }

    DataType<?> getDataType();

    <T> T asObject();

    default <T> T asObject(Class<T> clazz) {
        return asObject();
    }

    byte[] getData();

    // Useful if you need the data type
    // to be included in the array
    byte[] getFullData();
}
