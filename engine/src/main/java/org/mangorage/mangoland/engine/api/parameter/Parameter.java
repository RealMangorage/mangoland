package org.mangorage.mangoland.engine.api.parameter;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.api.variable.Variable;
import org.mangorage.mangoland.engine.internal.parameter.ParameterImpl;

public sealed interface Parameter permits ParameterImpl {
    static Parameter of(Variable variable) {
        return new ParameterImpl(variable);
    }

    boolean is(final DataType<?> dataType);

    <T> T asObject();

    default <T> T asObject(final Class<T> tClass) {
        return asObject();
    }

    DataType<?> getDataType();

    Variable getVariable();

    // Useful if you need the data type & parameter
    //   markers to be included in the array
    byte[] getFullData();
}
