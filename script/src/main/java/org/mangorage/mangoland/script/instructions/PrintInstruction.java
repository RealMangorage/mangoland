package org.mangorage.mangoland.script.instructions;

import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;
import org.mangorage.mangoland.engine.constants.CommonFlags;
import org.mangorage.mangoland.engine.exception.CompileException;
import org.mangorage.mangoland.engine.api.instruction.Instruction;
import org.mangorage.mangoland.script.util.GeneralUtil;
import org.mangorage.mangoland.script.util.StringUtil;
import org.mangorage.mangoland.script.ScriptDataTypes;

public final class PrintInstruction implements Instruction {
    @Override
    public int execute(byte[] instruction, final RuntimeEnv env) {

        final var params = GeneralUtil.getParameters(instruction, env);
        final var param = params[0]; // TYPE LENGTH DATA

        if (ScriptDataTypes.VARIABLE_TYPE.equals(param.getDataType())) {
            System.out.println(
                    env.getPersistence().getVariable(param.getVariable().getData(CommonFlags.includeData)).asObject(String.class)
            );
        } else if (ScriptDataTypes.STRING_TYPE.equals(param.getDataType())) {
            System.out.println(param.asObject(String.class));
        }

        return 0;
    }

    @Override
    public byte[] compile(final String code, final CompileEnv env) {
        final var params = StringUtil.extractQuotedStrings(code, env);
        if (params.length != 1) throw new CompileException("Unable to compile. Can only have one parameter for #print, got " + params.length);
        return params[0].getData(CommonFlags.includeAll);
    }
}
