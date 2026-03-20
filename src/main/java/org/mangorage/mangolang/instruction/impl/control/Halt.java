package org.mangorage.mangolang.instruction.impl.control;

import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class Halt implements Instruction {
    public void execute(VMEnvironment env) {
        env.setRunning(false);
    }
}
