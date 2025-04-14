package org.mangorage.mangoland.engine.api.env;

import org.mangorage.mangoland.engine.api.Persistence;
import org.mangorage.mangoland.engine.internal.env.RuntimeEnvImpl;

public sealed interface RuntimeEnv extends CompileEnv permits RuntimeEnvImpl {
    static RuntimeEnv of(final CompileEnv compileEnv, final Persistence persistence) {
        return new RuntimeEnvImpl(compileEnv, persistence);
    }

    void setVariable(final byte[] key, final byte[] value);
    byte[] getVariable(final byte[] key);
}
