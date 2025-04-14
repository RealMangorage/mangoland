package org.mangorage.mangoland.script.instructions;

import org.mangorage.mangoland.engine.api.Instruction;
import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;

public final class InvalidInstruction implements Instruction {
    public static final Instruction INSTANCE = new InvalidInstruction();

    InvalidInstruction() {}

    @Override
    public void process(final byte[] instruction, final RuntimeEnv env) {
        throw new IllegalStateException("Invalid Instruction");
    }

    @Override
    public byte[] compile(final String code, final CompileEnv env) {
        throw new IllegalStateException("Invalid Instruction");
    }
}
