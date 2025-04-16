package org.mangorage.mangoland.script.instructions;

import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;
import org.mangorage.mangoland.engine.api.instruction.Instruction;

public final class CallInstruction implements Instruction {
    @Override
    public int execute(final byte[] instruction, final RuntimeEnv env) {
        return 0;
    }

    @Override
    public byte[] compile(final String code, final CompileEnv env) {
        return new byte[0];
    }
}
