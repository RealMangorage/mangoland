package org.mangorage.mangolang.instruction.impl.comparison;

import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class GreaterThanZero implements Instruction {
    @Override
    public void execute(VMEnvironment env) {
        var obj = env.getStack().pop();
        int val = MangolangObjects.requireIntegerValue(obj, "greater_than_zero");
        env.getStack().push(MangolangObjects.booleanObject(val > 0));
    }
}