package org.mangorage.mangoland.script.instructions;

import org.mangorage.mangland.java.api.IScriptProvider;
import org.mangorage.mangoland.engine.api.env.CompileEnv;
import org.mangorage.mangoland.engine.api.env.RuntimeEnv;
import org.mangorage.mangoland.engine.api.instruction.Instruction;
import org.mangorage.mangoland.engine.exception.CompileException;
import org.mangorage.mangoland.script.util.StringUtil;

public final class CallInstruction implements Instruction {
    private final IScriptProvider provider;

    public CallInstruction(final IScriptProvider provider) {
        this.provider = provider;
    }

    @Override
    public int execute(final byte[] instruction, final RuntimeEnv env) {
        return 0;
    }

    @Override
    public byte[] compile(final String code, final CompileEnv env) {
        var params = StringUtil.extractQuotedStrings(code, env);
        if (params.length != 1) throw new CompileException("Expected 1 parameters, got " + params.length);
        return new byte[0];
    }
}
