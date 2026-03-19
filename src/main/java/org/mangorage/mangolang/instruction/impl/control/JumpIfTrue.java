package org.mangorage.mangolang.instruction.impl.control;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VM;
import java.util.List;

public class JumpIfTrue implements Instruction {
    public JumpIfTrue() {
    }

    @Override
    public void execute(VM vm) {
        final var booleanValue = vm.getStack().pop();

        // Read the two target addresses emitted by the compiler
        int trueAddr = vm.next();
        int falseAddr = vm.next();

        if (booleanValue == 1) {
            vm.ip = trueAddr;
        } else {
            vm.ip = falseAddr;
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
