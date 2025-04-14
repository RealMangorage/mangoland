package org.mangorage.mangoland.engine.internal.env;

import org.mangorage.mangoland.engine.api.DataType;
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
    public void setVariable(byte[] key, byte[] value) {
        persistence.setVariable(key, value);
    }

    @Override
    public byte[] getVariable(byte[] key) {
        return persistence.getVariable(key);
    }

    @Override
    public DataType<?> getDataType(ByteArrayKey byteArrayKey) {
        return compileEnv.getDataType(byteArrayKey);
    }

    @Override
    public DataType<?> getDataType(String keyword) {
        return compileEnv.getDataType(keyword);
    }
}
