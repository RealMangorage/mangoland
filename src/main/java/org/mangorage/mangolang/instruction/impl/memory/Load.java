package org.mangorage.mangolang.instruction.impl.memory;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

@AutoRegisterInstruction
public final class Load implements Instruction {

    @Override
    public void execute(VMEnvironment env) {
        int index = env.next();       // read variable index
        org.mangorage.mangolang.object.MangolangObject value = env.getLocal(index); // get value from locals
        env.getStack().push(value);      // push onto stack
    }

    @Override
    public void emitBytecode(List<Integer> output, CompilerContext ctx, Object... args) {
        if (args.length != 1) throw new RuntimeException("Load requires 1 argument (variable name or index)");

        String nameOrValue = args[0].toString();

        int index;
        // dynamically resolve: either a variable name or a literal index
        if (ctx.hasVariable(nameOrValue)) {
            index = ctx.getVariableIndex(nameOrValue);
        } else {
            // fallback: treat as literal integer (for advanced scenarios)
            index = Integer.parseInt(nameOrValue);
        }

        output.add(index);
    }
}