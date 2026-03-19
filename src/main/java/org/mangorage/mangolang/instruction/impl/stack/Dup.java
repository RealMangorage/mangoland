package org.mangorage.mangolang.instruction.impl.stack;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VM;
import java.util.List;

public class Dup implements Instruction {
    @Override
    public void execute(VM vm) {
        if (vm.getStack().isEmpty()) {
            throw new RuntimeException("Stack underflow: Cannot dup an empty stack");
        }

        // Peek at the top value without removing it, then push a copy
        int topValue = vm.getStack().peek();
        vm.getStack().push(topValue);
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
