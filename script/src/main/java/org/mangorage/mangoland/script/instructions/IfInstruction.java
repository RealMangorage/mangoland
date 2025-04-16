package org.mangorage.mangoland.script.instructions;

import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;
import org.mangorage.mangoland.engine.api.instruction.Instruction;
import org.mangorage.mangoland.engine.util.ByteUtil;
import org.mangorage.mangoland.script.exception.CompileException;
import org.mangorage.mangoland.script.util.GeneralUtil;
import org.mangorage.mangoland.script.util.StringUtil;

import java.util.Arrays;

public final class IfInstruction implements Instruction {
    @Override
    public int execute(final byte[] instruction, final RuntimeEnv env) {
        final var params = GeneralUtil.getParameters(instruction, env);

        if (
                !Arrays.equals(
                        params[0].getVariable().getFullData(),
                        params[1].getVariable().getFullData()
                )
        ) {
            return params[2].asObject();
        }

        return 0;
    }

    @Override
    public byte[] compile(final String code, final CompileEnv env) {
        final var params = StringUtil.extractQuotedStrings(code, env);
        if (params.length != 3) throw new CompileException("Expected 3 parameters, got " + params.length);
        if (!params[2].getDataType().equals(env.getDataType("integer"))) throw new CompileException("Can only have integer type as return value for If Instructions");
        return ByteUtil.merge(
                params[0].getFullData(),
                params[1].getFullData(),
                params[2].getFullData()
        );
    }
}
