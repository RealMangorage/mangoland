package org.mangorage.mangoland.engine.api.variable;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.internal.variable.VariableImpl;

/**
    Represents just the DataType and the Data associated with it

    Useful for compiling/execution, with many useful functions...
 */
public sealed interface Variable permits VariableImpl {
    static Variable of(final DataType<?> dataType, final byte[] data) {
        return new VariableImpl(dataType, data);
    }

    DataType<?> getDataType();

    <T> T asObject();

    default <T> T asObject(final Class<T> clazz) {
        return asObject();
    }

    byte[] getData(final int flags);
}
