package org.mangorage.mangoland.core.instruction.impl;

import org.mangorage.mangoland.core.datatype.DataTypes;
import org.mangorage.mangoland.core.instruction.Instruction;
import org.mangorage.mangoland.core.persistance.Persistence;

public final class InvalidInstruction implements Instruction {
    public static final Instruction INSTANCE = new InvalidInstruction();

    InvalidInstruction() {}

    @Override
    public void process(final byte[] instruction, final Persistence persistence, final DataTypes dataTypes) {
        throw new IllegalStateException("Invalid Instruction");
    }

    @Override
    public byte[] compile(String code, DataTypes dataTypes) {
        throw new IllegalStateException("Invalid Instruction");
    }
}
