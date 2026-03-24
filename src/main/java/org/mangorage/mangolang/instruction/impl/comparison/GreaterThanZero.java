package org.mangorage.mangolang.instruction.impl.comparison;

import org.mangorage.mangolang.instruction.register.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.OperationType;
import org.mangorage.mangolang.object.impl.IntegerMLObject;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction(id = "greater_then_zero")
public final class GreaterThanZero implements Instruction {
    @Override
    public void execute(VMEnvironment env) {
        var obj = env.getStack().pop();
        env.getStack().push(MangolangObjects.applyOperation(obj, new IntegerMLObject(0), OperationType.GREATER_THAN));
    }
}