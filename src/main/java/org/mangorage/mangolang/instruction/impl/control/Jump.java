package org.mangorage.mangolang.instruction.impl.control;

import org.mangorage.mangolang.instruction.register.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class Jump implements Instruction {
    @Override
    public void execute(VMEnvironment env) {
        // Update the instruction pointer to the argument provided
        env.setIp(env.next());
    }
}