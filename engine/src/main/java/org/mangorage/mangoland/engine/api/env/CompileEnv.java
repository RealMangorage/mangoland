package org.mangorage.mangoland.engine.api.env;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.engine.internal.env.CompileEnvImpl;

import java.util.Arrays;

/**
    Used to assemble a Set of {@link DataType}'s that are valid for compiling.
 */
public sealed interface CompileEnv permits RuntimeEnv, CompileEnvImpl {
    default DataType<?> getDataType(final byte[] key) {
        return getDataType(ByteArrayKey.of(key));
    }

    DataType<?> getDataType(final ByteArrayKey byteArrayKey);
    DataType<?> getDataType(final String keyword);

    default boolean isDataType(final String keyword, final DataType<?> dataType) {
        return Arrays.equals(
                getDataType(keyword).getInternalId().get(),
                dataType.getInternalId().get()
        );
    }
}
