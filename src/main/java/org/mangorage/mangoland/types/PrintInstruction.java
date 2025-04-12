package org.mangorage.mangoland.types;

import org.mangorage.mangoland.Instruction;
import org.mangorage.mangoland.data.ByteConstants;
import org.mangorage.mangoland.persistance.Persistence;
import org.mangorage.mangoland.util.ByteUtil;
import org.mangorage.mangoland.util.StringUtil;

import java.util.Arrays;

public final class PrintInstruction implements Instruction {
    @Override
    public void process(byte[] instruction, final Persistence persistence) {
        if (instruction.length > 4 && hasVariable(instruction)) {
            System.out.println(
                    new String(
                            persistence.getVariable(
                                    Arrays.copyOfRange(instruction, 4, instruction.length)
                            )
                    )
            );
        } else {
            System.out.println(
                    new String(
                            instruction
                    )
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
            return ByteUtil.merge(ByteConstants.VARIABLE_INST, parameter.substring(parameter.indexOf("$") + 1).getBytes());
        } else {
            return parameter.getBytes();
        }
    }
}
