package org.mangorage.mangolang.instruction.impl.stack;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.impl.IntegerMLObject;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

@AutoRegisterInstruction
public final class Push implements Instruction {
    public void execute(VMEnvironment env) {
        // Next int is an immediate literal; wrap it as an IntegerMLObject
        int literal = env.next();
        MangolangObject obj = new IntegerMLObject(literal);
        env.getStack().push(obj);
    }

    @Override
    public void emitBytecode(List<Integer> output, CompilerContext ctx, Object... args) {
        if (args.length != 1) {
            throw new RuntimeException("Push requires exactly 1 argument");
        }

        // Push expects a numeric value
        try {
            int value = Integer.parseInt(args[0].toString());
            output.add(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Push argument must be an integer: " + args[0]);
        }
    }
}
