package org.mangorage.mangoland.script.instructions;

import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;
import org.mangorage.mangoland.script.exception.CompileException;
import org.mangorage.mangoland.engine.api.instruction.Instruction;
import org.mangorage.mangoland.script.util.ParameterConstants;
import org.mangorage.mangoland.engine.util.ByteUtil;
import org.mangorage.mangoland.script.util.StringUtil;
import org.mangorage.mangoland.script.ScriptDataTypes;

import java.util.Arrays;

public final class PrintInstruction implements Instruction {
    @Override
    public void process(byte[] instruction, final RuntimeEnv env) {
        var params = ByteUtil.extractBetween(instruction, ParameterConstants.PARAMETER_START.get(), ParameterConstants.PARAMETER_END.get());
        if (params.length > 0) {
            var param = params[0]; // TYPE LENGTH DATA

            byte[] TYPE = Arrays.copyOfRange(param, 0, 4);
            int length = ByteUtil.bytesToInt(Arrays.copyOfRange(param, 4, 8));
            byte[] data = Arrays.copyOfRange(param, 8, param.length);

            if (ScriptDataTypes.VARIABLE.equals(TYPE)) {
                System.out.println(
                        new String(
                                env.getPersistence().getVariable(data)
                        )
                );
            } else if (ScriptDataTypes.STRING_TYPE.equals(TYPE)) {
                System.out.println(
                        new String(
                                data
                        )
                );
            }
        }
    }

    @Override
    public byte[] compile(final String code, final CompileEnv env) {
        var array = StringUtil.extractQuotedStrings(code);
        if (array.length != 1) throw new CompileException("Unable to compile. Can only have one parameter for #print, got " + array.length);

        var paramPre = array[0];
        var variable = paramPre.startsWith("$");
        var param = variable ? paramPre.replaceFirst("\\$", "").getBytes() : paramPre.getBytes();

        return ByteUtil.merge(
                ParameterConstants.PARAMETER_START.get(),
                ByteUtil.merge(
                        variable ? ScriptDataTypes.VARIABLE.get() : ScriptDataTypes.STRING_TYPE.get(),
                        ByteUtil.merge(
                                ByteUtil.intToBytes(param.length),
                                ByteUtil.merge(
                                        param,
                                        ParameterConstants.PARAMETER_END.get()
                                )
                        )
                )
        );
    }
}
