package org.mangorage.mangolang.instruction.impl.control;

import org.mangorage.mangolang.instruction.register.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class Jump implements Instruction {
    @Override
    public void execute(VMEnvironment env) {
        int low = env.next() & 0xFF;
        int high = env.next() & 0xFF;
        env.setIp((high << 8) | low);
    }
}