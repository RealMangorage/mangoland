package org.mangorage.mangolang.instruction.impl.arithmetic;

import org.mangorage.mangolang.instruction.register.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.impl.IntegerMLObject;
import org.mangorage.mangolang.object.OperationType;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class Decrement implements Instruction {
    @Override
    public void execute(VMEnvironment env) {
        var obj = env.getStack().pop();
        env.getStack().push(MangolangObjects.applyOperation(obj, new IntegerMLObject(1), OperationType.SUBTRACT));
    }
}
