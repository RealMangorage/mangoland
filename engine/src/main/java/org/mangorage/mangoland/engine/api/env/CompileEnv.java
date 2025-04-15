package org.mangorage.mangoland.engine.api.env;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.engine.internal.env.CompileEnvImpl;

public sealed interface CompileEnv permits RuntimeEnv, CompileEnvImpl {
    DataType<?> getDataType(ByteArrayKey byteArrayKey);
    DataType<?> getDataType(String keyword);
}
