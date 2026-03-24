package org.mangorage.mangolang.instruction.impl.timing;

import org.mangorage.mangolang.instruction.register.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class Sleep implements Instruction {

    @Override
    public void execute(VMEnvironment env) {
        var obj = env.getStack().pop();
        int durationMs = MangolangObjects.requireIntegerValue(obj, "sleep");
        try {
            Thread.sleep(durationMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Sleep interrupted!");
        }
    }
}