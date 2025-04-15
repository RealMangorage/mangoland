package org.mangorage.mangoland.engine.api.env;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.engine.internal.env.CompileEnvImpl;

public sealed interface CompileEnv permits RuntimeEnv, CompileEnvImpl {
    default DataType<?> getDataType(final byte[] key) {
        return getDataType(ByteArrayKey.of(key));
    }

    DataType<?> getDataType(final ByteArrayKey byteArrayKey);
    DataType<?> getDataType(final String keyword);
}
