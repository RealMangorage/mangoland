package org.mangorage.mangolang.instruction.impl.stack;

import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class Dup implements Instruction {
    @Override
    public void execute(VMEnvironment env) {
        if (env.getStack().isEmpty()) {
            throw new RuntimeException("Stack underflow: Cannot dup an empty stack");
        }

        // Peek at the top value without removing it, then push a copy
        int topValue = env.getStack().peek();
        env.getStack().push(topValue);
    }
}
