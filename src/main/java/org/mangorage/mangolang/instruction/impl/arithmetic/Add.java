package org.mangorage.mangolang.instruction.impl.arithmetic;

import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.object.impl.IntegerMLObject;
import org.mangorage.mangolang.object.impl.StringMLObject;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class Add implements Instruction {
    public void execute(VMEnvironment env) {
        var bObj = env.getStack().pop();
        var aObj = env.getStack().pop();

        if (aObj instanceof IntegerMLObject aInt && bObj instanceof IntegerMLObject bInt) {
            env.getStack().push(new IntegerMLObject(aInt.getValue() + bInt.getValue()));
            return;
        }

        env.getStack().push(new StringMLObject(asDisplayString(aObj) + asDisplayString(bObj)));
    }

    private String asDisplayString(MangolangObject value) {
        return MangolangObjects.toDisplayString(value);
    }
}
