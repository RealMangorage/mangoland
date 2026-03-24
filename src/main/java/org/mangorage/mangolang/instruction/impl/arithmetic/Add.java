package org.mangorage.mangolang.instruction.impl.arithmetic;

import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.impl.IntegerMLObject;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class Add implements Instruction {
    public void execute(VMEnvironment env) {
        var bObj = env.getStack().pop();
        var aObj = env.getStack().pop();
        int b = MangolangObjects.requireIntegerValue(bObj, "add");
        int a = MangolangObjects.requireIntegerValue(aObj, "add");
        env.getStack().push(new IntegerMLObject(a + b));
    }
}
