package org.mangorage.mangoland.engine.api.instruction;

import org.mangorage.mangoland.engine.api.env.CompileEnv;

public sealed interface InstructionSet permits org.mangorage.mangoland.engine.internal.instruction.InstructionSetImpl {
    void process(final byte[] instructions, final CompileEnv env);
    byte[] compile(final String[] code, final CompileEnv env);
}
