package org.mangorage.mangolang.instruction.impl.comparison;

import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class Equals implements Instruction {
    @Override
    public void execute(VMEnvironment env) {
        int b = env.getStack().pop();
        int a = env.getStack().pop();
        env.getStack().push(a == b ? 1 : 0);
    }
}
