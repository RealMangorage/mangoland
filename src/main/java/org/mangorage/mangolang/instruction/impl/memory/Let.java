package org.mangorage.mangolang.instruction.impl.memory;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VM;

import java.util.List;

public final class Let implements Instruction {

    @Override
    public void execute(VM vm) {
        int index = vm.next();  // variable index
        int value = vm.next();  // value to store
        vm.setLocal(index, value);
    }

    @Override
    public int getArgCount() { return 2; }

    @Override
    public void emitBytecode(List<Integer> output, CompilerContext ctx, Object... args) {
        if (args.length != 2)
            throw new RuntimeException("Let instruction requires variable name and value");

        String name = args[0].toString();

        // Dynamically resolve or declare variable

        int index = ctx.hasVariable(name) ? ctx.getVariableIndex(name) : ctx.declareVariable(name);

        // Value can be integer literal or another variable name
        int value;
        try {
            value = Integer.parseInt(args[1].toString());
        } catch (NumberFormatException e) {
            // Treat it as variable reference
            value = ctx.getVariableIndex(args[1].toString());
        }

        output.add(index);
        output.add(value);
    }
}