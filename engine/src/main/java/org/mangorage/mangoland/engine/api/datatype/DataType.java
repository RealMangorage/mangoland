package org.mangorage.mangoland.engine.api.datatype;

import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.engine.api.parameter.Parameter;
import org.mangorage.mangoland.engine.api.variable.Variable;

import java.util.function.Function;

/**
    Represents a {@link DataType} that can be used to define what an Object is

    Such as Integer/String/Boolean/etc
 */
public interface DataType<T> {
    static <T> DataType<T> of(final byte[] key, final Function<ByteArrayKey, DataType<T>> function) {
        return of(ByteArrayKey.of(key), function);
    }

    static <T> DataType<T> of(final ByteArrayKey key, final Function<ByteArrayKey, DataType<T>> function) {
        return function.apply(key);
    }

    default Variable createVariable(final byte[] data) {
        return Variable.of(this, data);
    }

    default Parameter createParameter(final byte[] data) {
        return Parameter.of(createVariable(data));
    }

    ByteArrayKey getDataType();

    T asObject(byte[] data);

    byte[] cast(DataType<?> from, byte[] data);

    byte[] compile(String code);
}
