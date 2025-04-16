package org.mangorage.mangoland.script.instructions;

import org.mangorage.mangoland.engine.api.variable.Variable;
import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;
import org.mangorage.mangoland.script.exception.CompileException;
import org.mangorage.mangoland.engine.api.instruction.Instruction;
import org.mangorage.mangoland.engine.util.ByteUtil;
import org.mangorage.mangoland.script.util.GeneralUtil;
import org.mangorage.mangoland.script.util.StringUtil;

public final class ParseInstruction implements Instruction {
    @Override
    public int execute(final byte[] instruction, final RuntimeEnv env) {
        final var params = GeneralUtil.getParameters(instruction, env);

        final var variable = env.getPersistence().getVariable(params[0].getVariable().getData());
        final var destination = params[1];
        final var destinationData = destination.getVariable().getData();
        final var destinationDataType = destination.getDataType();

        env.getPersistence()
                .setVariable(
                        destinationData,
                        Variable.of(
                                destinationDataType,
                                destinationDataType.cast(
                                        variable.getDataType(),
                                        variable.getData()
                                )
                        )
                );

        return 0;
    }

    @Override
    public byte[] compile(final String code, final CompileEnv env) {
        final var params = StringUtil.extractQuotedStrings(code, env);
        if (params.length != 2) throw new CompileException("Expected 2 parameters, got " + params.length);
        return ByteUtil.merge(
                params[0].getFullData(),
                params[1].getFullData()
        );
    }
}
