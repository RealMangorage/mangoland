package org.mangorage.mangoland.engine.api.instruction;

import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;

/**
    Just makes your program crash if it tries to use an invalid instruction.

    TODO: Might make it not crash on compile though.
 */
public final class InvalidInstruction implements Instruction {
    public static final Instruction INSTANCE = new InvalidInstruction();

    InvalidInstruction() {}

    @Override
    public int execute(final byte[] instruction, final RuntimeEnv env) {
        throw new IllegalStateException("Invalid Instruction");
    }

    @Override
    public byte[] compile(final String code, final CompileEnv env) {
        throw new IllegalStateException("Invalid Instruction");
    }
}
