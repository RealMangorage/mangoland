package org.mangorage.mangolang.instruction.impl.arithmetic;

import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class Multiply implements Instruction {
    @Override
    public void execute(VMEnvironment env) {
        var bObj = env.getStack().pop();
        var aObj = env.getStack().pop();
        int b = ((org.mangorage.mangolang.object.impl.IntegerMLObject) bObj).getValue();
        int a = ((org.mangorage.mangolang.object.impl.IntegerMLObject) aObj).getValue();
        env.getStack().push(new org.mangorage.mangolang.object.impl.IntegerMLObject(a * b));
    }
}