package org.mangorage.mangoland.script.instructions;

import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;
import org.mangorage.mangoland.script.exception.CompileException;
import org.mangorage.mangoland.engine.api.instruction.Instruction;
import org.mangorage.mangoland.script.util.ParameterConstants;
import org.mangorage.mangoland.engine.util.ByteUtil;
import org.mangorage.mangoland.script.util.GeneralUtil;
import org.mangorage.mangoland.script.util.StringUtil;
import org.mangorage.mangoland.script.ScriptDataTypes;

public final class AddInstruction implements Instruction {

    @Override
    public void process(byte[] instruction, final RuntimeEnv env) {
        var params = GeneralUtil.getParameters(instruction, env);
        if (params.length == 3) {
            var paramOne = params[0];
            var paramTwo = params[1];
            var paramThree = params[2];

            int a = 0;
            int b = 0;

            if (paramOne.is(ScriptDataTypes.INTEGER_TYPE)) {
                a = paramOne.asObject();
            } else if (paramOne.is(ScriptDataTypes.VARIABLE)) {
                a = ByteUtil.bytesToInt(env.getPersistence().getVariable(paramOne.getData()));
            }

            if (paramTwo.is(ScriptDataTypes.INTEGER_TYPE)) {
                b = paramTwo.asObject();
            } else if (paramTwo.is(ScriptDataTypes.VARIABLE)) {
                b = ByteUtil.bytesToInt(env.getPersistence().getVariable(paramTwo.getData()));
            }

            if (paramThree.is(ScriptDataTypes.VARIABLE)) {
                env.getPersistence().setVariable(paramThree.getData(), ByteUtil.intToBytes(a + b));
            }
        }
    }


    @Override
    public byte[] compile(String code, CompileEnv env) {
        var params = StringUtil.extractQuotedStrings(code);
        if (params.length != 3) throw new CompileException("Expected 3 parameters, got " + params.length);

        var result = new byte[0];

        for (int i = 0; i < params.length; i++) {
            final var param = params[i];
            final boolean isVariable = param.startsWith("$");
            final byte[] data = isVariable ? param.replaceFirst("\\$", "").getBytes() : ByteUtil.intToBytes(Integer.parseInt(param));

            if (!isVariable && i == 2) throw new CompileException("Output/3rd parameter needs to be a variable...");

            result =
                    ByteUtil.merge(
                            result,
                            ByteUtil.merge(
                                    ParameterConstants.PARAMETER_START.get(),
                                    ByteUtil.merge(
                                            isVariable ? ScriptDataTypes.VARIABLE.get() : ScriptDataTypes.INTEGER_TYPE.get(),
                                            ByteUtil.merge(
                                                    ByteUtil.intToBytes(data.length),
                                                    ByteUtil.merge(
                                                            data,
                                                            ParameterConstants.PARAMETER_END.get()
                                                    )
                                            )
                                    )
                            )
                    );
        }

        return result;
    }
}
