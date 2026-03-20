package org.mangorage.mangolang.instruction.impl.arithmetic;

import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class Decrement implements Instruction {
    @Override
    public void execute(VMEnvironment env) {
        int val = (int) env.getStack().pop();
        env.getStack().push(val - 1);
    }
}
