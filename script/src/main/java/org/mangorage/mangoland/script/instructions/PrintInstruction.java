package org.mangorage.mangoland.script.instructions;

import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;
import org.mangorage.mangoland.engine.constants.InstructionConstants;
import org.mangorage.mangoland.script.exception.CompileException;
import org.mangorage.mangoland.engine.api.instruction.Instruction;
import org.mangorage.mangoland.script.util.GeneralUtil;
import org.mangorage.mangoland.engine.util.ByteUtil;
import org.mangorage.mangoland.script.util.StringUtil;
import org.mangorage.mangoland.script.ScriptDataTypes;

public final class PrintInstruction implements Instruction {
    @Override
    public void process(byte[] instruction, final RuntimeEnv env) {

        var params = GeneralUtil.getParameters(instruction, env);
        var param = params[0]; // TYPE LENGTH DATA

        if (ScriptDataTypes.VARIABLE.equals(param.getDataType())) {
            System.out.println(
                    env.getPersistence().getVariable(param.getVariable().getData()).asObject(String.class)
            );
        } else if (ScriptDataTypes.STRING_TYPE.equals(param.getDataType())) {
            System.out.println(param.asObject(String.class));
        }

    }

    @Override
    public byte[] compile(final String code, final CompileEnv env) {
        var array = StringUtil.extractQuotedStrings(code);
        if (array.length != 1) throw new CompileException("Unable to compile. Can only have one parameter for #print, got " + array.length);

        var paramPre = array[0];
        var isVariable = paramPre.startsWith("$");
        var data = isVariable ? paramPre.replaceFirst("\\$", "").getBytes() : paramPre.getBytes();

        return isVariable ? ScriptDataTypes.VARIABLE.createParameter(data).getFullData() : ScriptDataTypes.STRING_TYPE.createParameter(data).getFullData();
    }
}
