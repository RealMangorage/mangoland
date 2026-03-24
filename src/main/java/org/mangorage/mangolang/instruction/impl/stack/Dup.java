package org.mangorage.mangolang.instruction.impl.stack;

import org.mangorage.mangolang.instruction.register.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class Dup implements Instruction {
    @Override
    public void execute(VMEnvironment env) {
        if (env.getStack().isEmpty()) {
            throw new RuntimeException("Stack underflow: Cannot dup an empty stack");
        }

        // Shallow duplicate: peek the top MangolangObject and push the same reference
        org.mangorage.mangolang.object.MangolangObject top = env.getStack().peek();
        env.getStack().push(top);
    }
}
