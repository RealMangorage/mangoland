package org.mangorage.mangolang.instruction.impl.memory;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.register.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

@AutoRegisterInstruction
public final class Store implements Instruction {

    @Override
    public void execute(VMEnvironment env) {
        int low = env.next() & 0xFF;
        int high = env.next() & 0xFF;
        int index = (high << 8) | low;
        org.mangorage.mangolang.object.MangolangObject value = env.getStack().pop(); // pop value from stack
        env.setLocal(index, value);       // store into local
    }

    @Override
    public void emitBytecode(List<Byte> output, CompilerContext ctx, Object... args) {
        if (args.length != 1)
            throw new RuntimeException("Store requires 1 argument (variable name)");

        String varName = args[0].toString();

        // AUTO-DECLARE if it doesn’t exist
        int index = ctx.hasVariable(varName) ? ctx.getVariableIndex(varName) : ctx.declareVariable(varName);

        output.add((byte) (index & 0xFF));
        output.add((byte) ((index >> 8) & 0xFF));
    }
}