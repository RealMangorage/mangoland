package org.mangorage.mangoland.engine.api.instruction;

import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;

/**

     INST    INST
    HEADER   DATA
    [1234]  [....]
 */
public interface Instruction {
    void process(final byte[] instruction, final RuntimeEnv runtimeEnv);
    byte[] compile(String code, CompileEnv dataTypes);
}
