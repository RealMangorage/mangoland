package org.mangorage.mangoland.script.instructions;

import org.mangorage.mangoland.engine.api.variable.Variable;
import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;
import org.mangorage.mangoland.engine.constants.CommonFlags;
import org.mangorage.mangoland.engine.exception.CompileException;
import org.mangorage.mangoland.engine.api.instruction.Instruction;
import org.mangorage.mangoland.engine.util.ByteUtil;
import org.mangorage.mangoland.script.util.GeneralUtil;
import org.mangorage.mangoland.script.util.StringUtil;
import org.mangorage.mangoland.script.ScriptDataTypes;

public final class AddInstruction implements Instruction {

    @Override
    public int execute(final byte[] instruction, final RuntimeEnv env) {
        final var params = GeneralUtil.getParameters(instruction, env);
        if (params.length == 3) {
            final var paramOne = params[0];
            final var paramTwo = params[1];
            final var paramThree = params[2];

            int a = 0;
            int b = 0;

            if (paramOne.is(ScriptDataTypes.INTEGER_TYPE)) {
                a = paramOne.asObject();
            } else if (paramOne.is(ScriptDataTypes.VARIABLE_TYPE)) {
                a = env.getPersistence().getVariable(paramOne.getVariable().getData(CommonFlags.includeData)).asObject();
            }

            if (paramTwo.is(ScriptDataTypes.INTEGER_TYPE)) {
                b = paramTwo.asObject();
            } else if (paramTwo.is(ScriptDataTypes.VARIABLE_TYPE)) {
                b = env.getPersistence().getVariable(paramTwo.getVariable().getData(CommonFlags.includeData)).asObject();
            }

            if (paramThree.is(ScriptDataTypes.VARIABLE_TYPE)) {
                env.getPersistence().setVariable(
                        paramThree.getVariable().getData(CommonFlags.includeData),
                        Variable.of(
                                ScriptDataTypes.INTEGER_TYPE,
                                ByteUtil.intToBytes(a + b)
                        )
                );
            }
        }

        return 0;
    }


    @Override
    public byte[] compile(final String code, final CompileEnv env) {
        final var params = StringUtil.extractQuotedStrings(code, env);
        if (params.length != 3) throw new CompileException("Expected 3 parameters, got " + params.length);
        return ByteUtil.merge(
                params[0].getData(CommonFlags.includeAll),
                params[1].getData(CommonFlags.includeAll),
                params[2].getData(CommonFlags.includeAll)
        );
    }
}
