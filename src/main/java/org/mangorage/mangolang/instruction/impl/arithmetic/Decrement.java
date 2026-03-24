package org.mangorage.mangolang.instruction.impl.arithmetic;

import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.impl.IntegerMLObject;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class Decrement implements Instruction {
    @Override
    public void execute(VMEnvironment env) {
        var obj = env.getStack().pop();
        int val = MangolangObjects.requireIntegerValue(obj, "decrement");
        env.getStack().push(new IntegerMLObject(val - 1));
    }
}
