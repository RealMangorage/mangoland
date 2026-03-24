package org.mangorage.mangolang.instruction.impl.memory;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

@AutoRegisterInstruction
public final class Let implements Instruction {

    @Override
    public void execute(VMEnvironment env) {
        int index = env.next();  // variable index
        MangolangObject value;

        if (env.peek() == MangolangObjects.OBJECT_PREFIX) {
            value = env.readObject();
        } else {
            int sourceIndex = env.next() & 0xFF;
            value = env.getLocal(sourceIndex);
        }

        env.setLocal(index, value);
    }

    @Override
    public void emitBytecode(List<Byte> output, CompilerContext ctx, Object... args) {
        if (args.length != 3)
            throw new RuntimeException("Let instruction requires variable name and value");

        String name = args[0].toString();

        // Dynamically resolve or declare variable

        int index = ctx.hasVariable(name) ? ctx.getVariableIndex(name) : ctx.declareVariable(name);

        output.add((byte) index);

        MangolangObject literal = MangolangObjects.literalFromToken(args[2].toString());
        if (literal != null) {
            MangolangObjects.emitObject(output, literal);
        } else {
            output.add((byte) ctx.getVariableIndex(args[2].toString()));
        }
    }
}