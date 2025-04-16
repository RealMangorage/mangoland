package org.mangorage.mangoland.engine.internal.env;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.ByteArrayKey;

import java.util.Map;

public final class CompileEnvImpl implements CompileEnv {

    private final Map<ByteArrayKey, DataType<?>> dataMap;
    private final Map<String, DataType<?>> dataMapByKeyword;

    public CompileEnvImpl(final Map<ByteArrayKey, DataType<?>> dataMap, final Map<String, DataType<?>> dataMapByKeyword) {
        this.dataMap = dataMap;
        this.dataMapByKeyword = dataMapByKeyword;
    }


    public DataType<?> getDataType(final ByteArrayKey byteArrayKey) {
        return dataMap.get(byteArrayKey);
    }

    public DataType<?> getDataType(final String keyword) {
        return dataMapByKeyword.get(keyword);
    }
}
