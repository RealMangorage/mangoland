package org.mangorage.mangoland.core.instruction.impl;

import org.mangorage.mangoland.core.datatype.DataType;
import org.mangorage.mangoland.core.datatype.DataTypes;
import org.mangorage.mangoland.core.exceptions.CompileException;
import org.mangorage.mangoland.core.instruction.Instruction;
import org.mangorage.mangoland.core.misc.ByteArrayKey;
import org.mangorage.mangoland.core.misc.ByteConstants;
import org.mangorage.mangoland.core.persistance.Persistence;
import org.mangorage.mangoland.core.util.ByteUtil;
import org.mangorage.mangoland.core.util.GeneralUtil;
import org.mangorage.mangoland.core.util.StringUtil;

public final class ParseInstruction implements Instruction {
    @Override
    public void process(byte[] instruction, Persistence persistence, DataTypes dataTypes) {
        var params = GeneralUtil.getParameters(instruction, dataTypes);

        // Variables
        var paramOne = persistence.getVariable(params[0].getData());
        var paramTwo = params[1].getData();

        // Data Types
        DataType<?> paramThree = dataTypes.getDataType(ByteArrayKey.of(params[2].getData()));
        DataType<?> paramFour = dataTypes.getDataType(ByteArrayKey.of(params[3].getData()));

        persistence.setVariable(
                paramTwo,
                paramThree.cast(
                        paramFour,
                        paramOne
                )
        );
    }

    @Override
    public byte[] compile(String code, DataTypes dataTypes) {
        var params = StringUtil.extractQuotedStrings(code);
        if (params.length != 4) throw new CompileException("Expected 4 parameters, got " + params.length);

        var result = new byte[0];

        for (String param : params) {
            if (param.startsWith("?")) {
                result = ByteUtil.merge(
                        result,
                        ByteUtil.merge(
                                ByteConstants.PARAMETER_START.get(),
                                ByteUtil.merge(
                                        DataTypes.DATA_TYPE.get(),
                                        ByteUtil.merge(
                                                ByteUtil.intToBytesLE(4),
                                                ByteUtil.merge(
                                                        dataTypes.getDataType(param.replaceFirst("\\?", "")).getDataType().get(),
                                                        ByteConstants.PARAMETER_END.get()
                                                )
                                        )
                                )
                        )
                );
                continue;
            }

            byte[] data = param.replaceFirst("\\$", "").getBytes();

            result = ByteUtil.merge(
                    result,
                    ByteUtil.merge(
                            ByteConstants.PARAMETER_START.get(),
                            ByteUtil.merge(
                                    DataTypes.VARIABLE.get(),
                                    ByteUtil.merge(
                                            ByteUtil.intToBytesLE(4),
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
