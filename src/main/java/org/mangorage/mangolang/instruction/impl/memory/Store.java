package org.mangorage.mangolang.instruction.impl.memory;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VM;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

public final class Store implements Instruction {

    @Override
    public void execute(VMEnvironment env) {
        int index = env.next();           // get the local variable index
        int value = env.getStack().pop(); // pop value from stack
        env.setLocal(index, value);       // store into local
    }

    @Override
    public int getArgCount() { return 1; }

    @Override
    public void emitBytecode(List<Integer> output, CompilerContext ctx, Object... args) {
        if (args.length != 1)
            throw new RuntimeException("Store requires 1 argument (variable name)");

        String varName = args[0].toString();

        // AUTO-DECLARE if it doesn’t exist
        int index = ctx.hasVariable(varName) ? ctx.getVariableIndex(varName) : ctx.declareVariable(varName);

        output.add(index);
    }
}