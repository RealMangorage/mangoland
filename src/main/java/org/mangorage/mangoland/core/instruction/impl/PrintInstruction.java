package org.mangorage.mangoland.core.instruction.impl;

import org.mangorage.mangoland.core.datatype.DataTypes;
import org.mangorage.mangoland.core.exceptions.CompileException;
import org.mangorage.mangoland.core.instruction.Instruction;
import org.mangorage.mangoland.core.misc.ByteConstants;
import org.mangorage.mangoland.core.persistance.Persistence;
import org.mangorage.mangoland.core.util.ByteUtil;
import org.mangorage.mangoland.core.util.StringUtil;

import java.util.Arrays;

public final class PrintInstruction implements Instruction {
    @Override
    public void process(byte[] instruction, final Persistence persistence, final DataTypes dataTypes) {
        var params = ByteUtil.extractBetween(instruction, ByteConstants.PARAMETER_START.get(), ByteConstants.PARAMETER_END.get());
        if (params.length > 0) {
            var param = params[0]; // TYPE LENGTH DATA

            byte[] TYPE = Arrays.copyOfRange(param, 0, 4);
            int length = ByteUtil.bytesToInt(Arrays.copyOfRange(param, 4, 8));
            byte[] data = Arrays.copyOfRange(param, 8, 8 + length);

            if (DataTypes.VARIABLE.equals(TYPE)) {
                System.out.println(
                        new String(
                                persistence.getVariable(data)
                        )
                );
            } else if (DataTypes.STRING_TYPE.equals(TYPE)) {
                System.out.println(
                        new String(
                                data
                        )
                );
            }
        }
    }

    @Override
    public byte[] compile(String code) {
        var array = StringUtil.extractQuotedStrings(code);
        if (array.length != 1) throw new CompileException("Unable to compile. Can only have one parameter for #print, got " + array.length);

        var paramPre = array[0];
        var variable = paramPre.startsWith("$");
        var param = variable ? paramPre.replaceFirst("\\$", "").getBytes() : paramPre.getBytes();

        return ByteUtil.merge(
                ByteConstants.PARAMETER_START.get(),
                ByteUtil.merge(
                        variable ? DataTypes.VARIABLE.get() : DataTypes.STRING_TYPE.get(),
                        ByteUtil.merge(
                                ByteUtil.intToBytesLE(param.length),
                                ByteUtil.merge(
                                        param,
                                        ByteConstants.PARAMETER_END.get()
                                )
                        )
                )
        );
    }
}
