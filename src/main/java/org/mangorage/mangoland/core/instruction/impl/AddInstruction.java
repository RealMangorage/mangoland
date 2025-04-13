package org.mangorage.mangoland.core.instruction.impl;

import org.mangorage.mangoland.core.datatype.DataTypes;
import org.mangorage.mangoland.core.exceptions.CompileException;
import org.mangorage.mangoland.core.instruction.Instruction;
import org.mangorage.mangoland.core.misc.ByteConstants;
import org.mangorage.mangoland.core.persistance.Persistence;
import org.mangorage.mangoland.core.util.ByteUtil;
import org.mangorage.mangoland.core.util.GeneralUtil;
import org.mangorage.mangoland.core.util.StringUtil;

public final class AddInstruction implements Instruction {

    @Override
    public void process(byte[] instruction, Persistence persistence, DataTypes dataTypes) {
        var params = GeneralUtil.getParameters(instruction, dataTypes);
        if (params.length == 3) {
            var paramOne = params[0];
            var paramTwo = params[1];
            var paramThree = params[2];

            int a = 0;
            int b = 0;

            if (paramOne.is(DataTypes.INTEGER_TYPE)) {
                a = paramOne.asObject();
            } else if (paramOne.is(DataTypes.VARIABLE)) {
                a = ByteUtil.bytesToInt(persistence.getVariable(paramOne.getData()));
            }

            if (paramTwo.is(DataTypes.INTEGER_TYPE)) {
                b = paramOne.asObject();
            } else if (paramTwo.is(DataTypes.VARIABLE)) {
                b = ByteUtil.bytesToInt(persistence.getVariable(paramTwo.getData()));
            }

            if (paramThree.is(DataTypes.VARIABLE)) {
                persistence.setVariable(paramThree.getData(), ByteUtil.intToBytesLE(a + b));
            }
        }
    }


    @Override
    public byte[] compile(String code, DataTypes dataTypes) {
        var params = StringUtil.extractQuotedStrings(code);
        if (params.length != 3) throw new CompileException("Expected 3 parameters, got " + params.length);

        var result = new byte[0];

        for (int i = 0; i < params.length; i++) {
            final var param = params[i];
            final boolean isVariable = param.startsWith("$");
            final byte[] data = isVariable ? param.replaceFirst("\\$", "").getBytes() : ByteUtil.intToBytesLE(Integer.parseInt(param));

            if (!isVariable && i == 2) throw new CompileException("Output/3rd parameter needs to be a variable...");

            result =
                    ByteUtil.merge(
                            result,
                            ByteUtil.merge(
                                    ByteConstants.PARAMETER_START.get(),
                                    ByteUtil.merge(
                                            isVariable ? DataTypes.VARIABLE.get() : DataTypes.INTEGER_TYPE.get(),
                                            ByteUtil.merge(
                                                    ByteUtil.intToBytesLE(data.length),
                                                    ByteUtil.merge(
                                                            data,
                                                            ByteConstants.PARAMETER_END.get()
                                                    )
                                            )
                                    )
                            )
                    );
        }

        return result;
    }
}
