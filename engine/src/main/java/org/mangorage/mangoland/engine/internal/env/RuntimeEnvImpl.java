package org.mangorage.mangoland.engine.internal.env;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.api.Persistence;
import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;
import org.mangorage.mangoland.engine.api.ByteArrayKey;

public final class RuntimeEnvImpl implements RuntimeEnv {

    private final CompileEnv compileEnv;
    private final Persistence persistence;

    public RuntimeEnvImpl(final CompileEnv compileEnv, final Persistence persistence) {
        this.compileEnv = compileEnv;
        this.persistence = persistence;
    }

    @Override
    public Persistence getPersistence() {
        return persistence;
    }

    @Override
    public DataType<?> getDataType(final ByteArrayKey byteArrayKey) {
        return compileEnv.getDataType(byteArrayKey);
    }

    @Override
    public DataType<?> getDataType(final String keyword) {
        return compileEnv.getDataType(keyword);
    }
}
