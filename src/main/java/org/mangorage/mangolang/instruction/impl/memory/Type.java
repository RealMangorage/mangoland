package org.mangorage.mangolang.instruction.impl.memory;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.instruction.register.AutoRegisterInstruction;
import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.impl.StringMLObject;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

@AutoRegisterInstruction
public final class Type implements Instruction {
    @Override
    public void execute(VMEnvironment env) {
        int low = env.next() & 0xFF;
        int high = env.next() & 0xFF;
        int index = (high << 8) | low;

        MangolangObject value = env.getLocal(index);
        if (value == null) {
            throw new RuntimeException("Cannot determine type of null value at variable index " + index);
        }

        env.getStack().push(new StringMLObject(value.typeName()));
    }

    @Override
    public void emitBytecode(List<Byte> output, CompilerContext ctx, Object... args) {
        if (args.length != 1) {
            throw new RuntimeException("Type requires 1 argument (variable name or index)");
        }

        String nameOrValue = args[0].toString();
        int index;
        if (ctx.hasVariable(nameOrValue)) {
            index = ctx.getVariableIndex(nameOrValue);
        } else {
            index = Integer.parseInt(nameOrValue);
        }

        output.add((byte) (index & 0xFF));
        output.add((byte) ((index >> 8) & 0xFF));
    }
}

