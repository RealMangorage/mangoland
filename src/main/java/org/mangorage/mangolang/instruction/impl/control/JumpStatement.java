package org.mangorage.mangolang.instruction.impl.control;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

public final class JumpStatement implements Instruction {
    private final int value;

    public JumpStatement(int value) {
        this.value = value;
    }

    @Override
    public void execute(VMEnvironment env) {
        final var booleanObj = env.getStack().pop();
        final int booleanValue = ((org.mangorage.mangolang.object.impl.IntegerMLObject) booleanObj).getValue();

        // Read the two target addresses emitted by the compiler
        int trueAddr = env.next();
        int falseAddr = env.next();

        if (booleanValue == value) {
            env.setIp(trueAddr);
        } else {
            env.setIp(falseAddr);
        }
    }

    public void emitBytecode(List<Byte> out, CompilerContext ctx, Object... args) {
        if (args.length != 2) throw new RuntimeException("jump_if_true requires two function names");

        int a = ctx.getFunction((String) args[0]);
        int b = ctx.getFunction((String) args[1]);

        out.add((byte) (a & 0xFF));
        out.add((byte) ((a >> 8) & 0xFF));
        out.add((byte) (b & 0xFF));
        out.add((byte) ((b >> 8) & 0xFF));
    }
}
