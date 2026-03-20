package org.mangorage.mangolang.instruction.impl.comparison;

import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class GreaterThanZero implements Instruction {
    @Override
    public void execute(VMEnvironment env) {
        int val = env.getStack().pop();
        env.getStack().push(val > 0 ? 1 : 0);
    }
}