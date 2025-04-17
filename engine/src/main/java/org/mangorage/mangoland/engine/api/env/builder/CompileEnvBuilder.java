package org.mangorage.mangoland.engine.api.env.builder;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.engine.internal.env.builder.CompileInvBuilderImpl;

/**
    This serves as a way to have {@link DataType}'s available when compiling.

    If you need the byte[] representation on DataType, you use {@link CompileEnv}
 */
public sealed interface CompileEnvBuilder permits CompileInvBuilderImpl {
    static CompileEnvBuilder create() {
        return new CompileInvBuilderImpl();
    }

    CompileEnvBuilder register(final String keyword, final ByteArrayKey dataTypeId, final DataType<?> dataType);

    default CompileEnvBuilder register(final String keyword, final DataType<?> dataType) {
        return register(keyword, dataType.getInternalId(), dataType);
    }

    CompileEnv build();
}

