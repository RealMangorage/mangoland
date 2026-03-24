package org.mangorage.mangolang.instruction.impl.arithmetic;

import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.OperationType;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class Subtract implements Instruction {
    @Override
    public void execute(VMEnvironment env) {
        var bObj = env.getStack().pop();
        var aObj = env.getStack().pop();
        env.getStack().push(MangolangObjects.applyOperation(aObj, bObj, OperationType.SUBTRACT));
    }
}
