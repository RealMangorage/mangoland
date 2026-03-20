package org.mangorage.mangolang.instruction.impl.timing;

import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class Sleep implements Instruction {

    @Override
    public void execute(VMEnvironment env) {
        int durationMs = env.getStack().pop(); // pop the value from the stack
        try {
            Thread.sleep(durationMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Sleep interrupted!");
        }
    }
}