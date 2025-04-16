package org.mangorage.mangoland.engine.api.parameter;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.api.variable.Variable;
import org.mangorage.mangoland.engine.internal.parameter.ParameterImpl;

/**
    Byte Array Representation -> [PARAM START, 4 Bytes] [DataType, 4 bytes] [Parameter Data, any bytes] [PARAM END, 4 Bytes]

    A {@link Variable} is just the DataType and Parameter Data all wrapped in a Variable Instance

    This class servers the ability to use {@link Parameter#getFullData()}, so you can easily
    construct the proper Byte Array for compiling/executing the code

    The {@link Variable#getFullData()} just returns a Byte Array with just the DataType and the Data associated
    with the object. DOES NOT include the PARAM START/END markers
 */
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
