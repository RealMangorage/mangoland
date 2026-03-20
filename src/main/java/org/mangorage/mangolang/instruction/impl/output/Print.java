package org.mangorage.mangolang.instruction.impl.output;

import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class Print implements Instruction {

    @Override
    public void execute(VMEnvironment env) {
        int value = (int) env.getStack().pop(); // pop dynamically
        env.getTerminal().println(value + "");
    }
}