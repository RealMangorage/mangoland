package org.mangorage.mangoland.script.instructions;

import org.mangorage.mangoland.engine.api.datatype.DataType;
import org.mangorage.mangoland.engine.api.variable.Variable;
import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;
import org.mangorage.mangoland.engine.constants.InstructionConstants;
import org.mangorage.mangoland.script.exception.CompileException;
import org.mangorage.mangoland.engine.api.instruction.Instruction;
import org.mangorage.mangoland.engine.api.ByteArrayKey;
import org.mangorage.mangoland.engine.util.ByteUtil;
import org.mangorage.mangoland.script.util.GeneralUtil;
import org.mangorage.mangoland.script.util.StringUtil;
import org.mangorage.mangoland.script.ScriptDataTypes;

public final class ParseInstruction implements Instruction {
    @Override
    public void process(final byte[] instruction, final RuntimeEnv env) {
        var params = GeneralUtil.getParameters(instruction, env);

        // Variables
        var paramOne = env.getPersistence().getVariable(params[0].getData());
        var paramTwo = params[1].getData();

        // Data Types
        DataType<?> paramThree = env.getDataType(ByteArrayKey.of(params[2].getData()));
        DataType<?> paramFour = env.getDataType(ByteArrayKey.of(params[3].getData()));

        env.getPersistence().setVariable(
                paramTwo,
                Variable.of(
                        paramThree,
                        paramThree.cast(
                                paramFour,
                                paramOne.getData()
                        )
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
                                InstructionConstants.PARAMETER_START.get(),
                                ByteUtil.merge(
                                    ScriptDataTypes.DATA_TYPE.getDataType().get(),
                                            ByteUtil.merge(
                                                    dataTypes.getDataType(param.replaceFirst("\\?", "")).getDataType().get(),
                                                    InstructionConstants.PARAMETER_END.get()
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
                            InstructionConstants.PARAMETER_START.get(),
                            ByteUtil.merge(
                                ScriptDataTypes.VARIABLE.getDataType().get(),
                                        ByteUtil.merge(
                                                data,
                                                InstructionConstants.PARAMETER_END.get()
                                        )
                                )
                    )
            );
        }


        return result;
    }
}
