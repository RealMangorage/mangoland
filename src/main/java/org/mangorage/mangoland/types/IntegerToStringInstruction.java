package org.mangorage.mangoland.types;

import org.mangorage.mangoland.Instruction;
import org.mangorage.mangoland.data.ByteConstants;
import org.mangorage.mangoland.persistance.Persistence;
import org.mangorage.mangoland.util.ByteUtil;
import org.mangorage.mangoland.util.StringUtil;

import java.util.Arrays;

public final class IntegerToStringInstruction implements Instruction {
    @Override
    public void process(byte[] instruction, Persistence persistence) {
        if (instruction.length > 4 && hasVariable(instruction)) {
            var variable = Arrays.copyOfRange(instruction, 4, instruction.length);
            persistence.setVariable(
                    variable,
                    String.valueOf(
                            ByteUtil.bytesToInt(persistence.getVariable(variable))
                    ).getBytes()
            );
        }
    }

    boolean hasVariable(byte[] bytes) {
        return Arrays.equals(ByteConstants.VARIABLE_INST, Arrays.copyOfRange(bytes, 0, 4));
    }

    @Override
    public byte[] compile(String code) {
        var array = StringUtil.extractQuotedStrings(code);
        if (array.length == 0) return new byte[0];

        var parameter = array[0];
        if (parameter.startsWith("$")) {
            return ByteUtil.merge(ByteConstants.VARIABLE_INST, parameter.replaceFirst("\\$", "").getBytes());
        }

        return new byte[0];
    }
}
