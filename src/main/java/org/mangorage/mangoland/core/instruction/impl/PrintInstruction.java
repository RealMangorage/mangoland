package org.mangorage.mangoland.core.instruction.impl;

import org.mangorage.mangoland.core.exceptions.CompileException;
import org.mangorage.mangoland.core.instruction.Instruction;
import org.mangorage.mangoland.core.misc.ByteConstants;
import org.mangorage.mangoland.core.persistance.Persistence;
import org.mangorage.mangoland.core.util.ByteUtil;
import org.mangorage.mangoland.core.util.StringUtil;

import java.util.Arrays;

public final class PrintInstruction implements Instruction {
    @Override
    public void process(byte[] instruction, final Persistence persistence) {
        var params = ByteUtil.extractBetween(instruction, ByteConstants.PARAMETER_START.get(), ByteConstants.PARAMETER_END.get());
        if (params.length > 0) {
            var param = params[0];

            int length = ByteUtil.bytesToInt(Arrays.copyOfRange(param, 0, 4));
            System.out.println(
                    new String(
                            Arrays.copyOfRange(
                                    param,
                                    4,
                                    4 + length
                            )
                    )
            );
        }
    }

    boolean hasVariable(byte[] bytes) {
        return Arrays.equals(ByteConstants.PARAMETER_START.get(), Arrays.copyOfRange(bytes, 0, 4));
    }

    @Override
    public byte[] compile(String code) {
        var array = StringUtil.extractQuotedStrings(code);
        if (array.length != 1) throw new CompileException("Unable to compile. Can only have one parameter for #print, got " + array.length);

        var param = array[0].replaceFirst("$", "").getBytes();

        return ByteUtil.merge(
                ByteConstants.PARAMETER_START.get(),
                ByteUtil.merge(
                        ByteUtil.intToBytesLE(param.length),
                        ByteUtil.merge(
                                param,
                                ByteConstants.PARAMETER_END.get()
                        )
                )
        );
    }
}
