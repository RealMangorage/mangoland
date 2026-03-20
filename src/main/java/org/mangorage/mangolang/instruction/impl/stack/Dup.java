package org.mangorage.mangolang.instruction.impl.stack;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VM;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

public class Dup implements Instruction {
    @Override
    public void execute(VMEnvironment env) {
        if (env.getStack().isEmpty()) {
            throw new RuntimeException("Stack underflow: Cannot dup an empty stack");
        }

        // Peek at the top value without removing it, then push a copy
        int topValue = env.getStack().peek();
        env.getStack().push(topValue);
    }

    @Override
    public int getArgCount() {
        return 0; // dup takes no arguments
    }

    @Override
    public void emitBytecode(List<Integer> output, CompilerContext ctx, Object... args) {
        // Nothing special to emit here, the Compiler handles the opcode insertion
    }
}
