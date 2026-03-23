package org.mangorage.mangolang.instruction.impl.memory;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

@AutoRegisterInstruction
public final class Let implements Instruction {

    @Override
    public void execute(VMEnvironment env) {
        int index = env.next();  // variable index
        int value = env.next();  // value to store (literal)
        env.setLocal(index, new org.mangorage.mangolang.object.impl.IntegerMLObject(value));
    }

    @Override
    public void emitBytecode(List<Byte> output, CompilerContext ctx, Object... args) {
        if (args.length != 3)
            throw new RuntimeException("Let instruction requires variable name and value");

        String name = args[0].toString();

        // Dynamically resolve or declare variable

        int index = ctx.hasVariable(name) ? ctx.getVariableIndex(name) : ctx.declareVariable(name);

        // Value can be integer literal or another variable name
        int value;
        try {
            value = Integer.parseInt(args[2].toString());
        } catch (NumberFormatException e) {
            // Treat it as variable reference
            value = ctx.getVariableIndex(args[2].toString());
        }

        output.add((byte) index);
        output.add((byte) value);
    }
}