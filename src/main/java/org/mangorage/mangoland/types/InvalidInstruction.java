package org.mangorage.mangoland.types;

import org.mangorage.mangoland.Instruction;
import org.mangorage.mangoland.persistance.Persistence;

public final class InvalidInstruction implements Instruction {
    public static final Instruction INSTANCE = new InvalidInstruction();

    InvalidInstruction() {}

    @Override
    public void process(final byte[] instruction, final Persistence persistence) {
        throw new IllegalStateException("Invalid Instruction");
    }

    @Override
    public byte[] compile(String code) {
        throw new IllegalStateException("Invalid Instruction");
    }
}
