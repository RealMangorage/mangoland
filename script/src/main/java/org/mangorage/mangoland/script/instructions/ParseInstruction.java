package org.mangorage.mangoland.script.instructions;

import org.mangorage.mangoland.engine.api.DataType;
import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;
import org.mangorage.mangoland.script.exception.CompileException;
import org.mangorage.mangoland.engine.api.Instruction;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.script.util.ByteConstants;
import org.mangorage.mangoland.script.util.ByteUtil;
import org.mangorage.mangoland.script.util.GeneralUtil;
import org.mangorage.mangoland.script.util.StringUtil;
import org.mangorage.mangoland.script.ScriptDataTypes;

public final class ParseInstruction implements Instruction {
    @Override
    public void process(final byte[] instruction, final RuntimeEnv env) {
        var params = GeneralUtil.getParameters(instruction, env);

        // Variables
        var paramOne = env.getVariable(params[0].getData());
        var paramTwo = params[1].getData();

        // Data Types
        DataType<?> paramThree = env.getDataType(ByteArrayKey.of(params[2].getData()));
        DataType<?> paramFour = env.getDataType(ByteArrayKey.of(params[3].getData()));

        env.setVariable(
                paramTwo,
                paramThree.cast(
                        paramFour,
                        paramOne
                )
        );
    }

    @Override
    public byte[] compile(final String code, final CompileEnv dataTypes) {
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
                                        ScriptDataTypes.DATA_TYPE.get(),
                                        ByteUtil.merge(
                                                ByteUtil.intToBytes(4),
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
                                    ScriptDataTypes.VARIABLE.get(),
                                    ByteUtil.merge(
                                            ByteUtil.intToBytes(4),
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
