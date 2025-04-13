package org.mangorage.mangoland.core.instruction.impl;

import org.mangorage.mangoland.core.datatype.DataTypes;
import org.mangorage.mangoland.core.instruction.Instruction;
import org.mangorage.mangoland.core.persistance.Persistence;
import org.mangorage.mangoland.core.util.ByteUtil;

public final class ParseInstruction implements Instruction {
    @Override
    public void process(byte[] instruction, Persistence persistence, DataTypes dataTypes) {
        var var = persistence.getVariable("1".getBytes());
        persistence.setVariable("1".getBytes(),                 String.valueOf(ByteUtil.bytesToInt(var)).getBytes());
    }

    @Override
    public byte[] compile(String code, DataTypes dataTypes) {
        return new byte[0];
    }
}
