package org.mangorage.mangoland.engine.api.env.builder;

import org.mangorage.mangoland.engine.api.DataType;
import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.engine.internal.env.builder.CompileInvBuilderImpl;

public sealed interface CompileEnvBuilder permits CompileInvBuilderImpl {
    static CompileEnvBuilder create() {
        return new CompileInvBuilderImpl();
    }

    CompileEnvBuilder register(final String keyword, final ByteArrayKey dataTypeId, final DataType<?> dataType);
    CompileEnv build();
}

