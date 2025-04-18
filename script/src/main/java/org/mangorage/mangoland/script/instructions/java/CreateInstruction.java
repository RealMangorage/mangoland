package org.mangorage.mangoland.script.instructions.java;

import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;
import org.mangorage.mangoland.engine.api.instruction.Instruction;
import org.mangorage.mangoland.engine.constants.CommonFlags;
import org.mangorage.mangoland.engine.exception.CompileException;
import org.mangorage.mangoland.engine.util.ByteUtil;
import org.mangorage.mangoland.script.util.GeneralUtil;
import org.mangorage.mangoland.script.util.StringUtil;

public final class CreateInstruction implements Instruction {

    @Override
    public int execute(byte[] instruction, RuntimeEnv env) {
        // create (string) 'org.mangorage.example' -> (var) '3'
        final var params = GeneralUtil.getParameters(instruction, env);
        if (params.length == 2) {
            // String
            final var clazz = params[0].asObject(String.class);

            // Var
            final var output = params[1].asObject(byte[].class);

            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            try {
                var clz = Class.forName(clazz, false, cl);
                env.getPersistence()
                        .setObject(output, clz.getConstructor().newInstance());
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }

        }
        return 0;
    }

    @Override
    public byte[] compile(String code, CompileEnv env) {
        final var params = StringUtil.extractQuotedStrings(code, env);
        if (params.length != 2) throw new CompileException("Expected 2 parameters, got " + params.length);
        return ByteUtil.merge(
                params[0].getData(CommonFlags.includeAll),
                params[1].getData(CommonFlags.includeAll)
        );
    }
}
