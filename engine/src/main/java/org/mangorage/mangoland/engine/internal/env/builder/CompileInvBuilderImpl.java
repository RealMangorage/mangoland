package org.mangorage.mangoland.engine.internal.env.builder;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.builder.CompileEnvBuilder;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.engine.internal.env.CompileEnvImpl;

import java.util.HashMap;
import java.util.Map;

public final class CompileInvBuilderImpl implements CompileEnvBuilder {
    private final Map<ByteArrayKey, DataType<?>> dataMap = new HashMap<>();
    private final Map<String, DataType<?>> dataMapByKeyword = new HashMap<>();

    public CompileInvBuilderImpl() {}

    public CompileEnvBuilder register(final String keyword, final ByteArrayKey dataTypeId, final DataType<?> dataType) {
        dataMap.put(dataTypeId, dataType);
        dataMapByKeyword.put(keyword, dataType);
        return this;
    }

    public CompileEnv build() {
        return new CompileEnvImpl(
                Map.copyOf(dataMap),
                Map.copyOf(dataMapByKeyword)
        );
    }
}
