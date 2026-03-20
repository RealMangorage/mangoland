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
        final var booleanValue = env.getStack().pop();

        // Read the two target addresses emitted by the compiler
        int trueAddr = env.next();
        int falseAddr = env.next();

        if (booleanValue == value) {
            env.setIp(trueAddr);
        } else {
            env.setIp(falseAddr);
        }
    }

    public int getArgCount() {
        return 2;
    }

    public void emitBytecode(List<Integer> out, CompilerContext ctx, Object... args) {
        if (args.length != 2) throw new RuntimeException("jump_if_true requires two function names");

        out.add(ctx.getFunction((String) args[0]));
        out.add(ctx.getFunction((String) args[1]));
    }
}
