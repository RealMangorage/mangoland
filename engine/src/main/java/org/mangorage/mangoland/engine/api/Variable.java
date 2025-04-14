package org.mangorage.mangoland.engine.api;

import org.mangorage.mangoland.engine.internal.VariableImpl;

public sealed interface Variable permits VariableImpl {
    static Variable of(final DataType<?> dataType, final byte[] data) {
        return new VariableImpl(dataType, data);
    }

    DataType<?> getDataType();
    byte[] getData();

    <T> T asObject();

    default <T> T asObject(Class<T> clazz) {
        return asObject();
    };
}
