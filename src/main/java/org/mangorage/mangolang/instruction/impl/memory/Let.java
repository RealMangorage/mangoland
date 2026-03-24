package org.mangorage.mangolang.instruction.impl.memory;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.register.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjectCompiler;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

@AutoRegisterInstruction
public final class Let implements Instruction {

    @Override
    public void execute(VMEnvironment env) {
        int targetLow = env.next() & 0xFF;
        int targetHigh = env.next() & 0xFF;
        int index = (targetHigh << 8) | targetLow;
        MangolangObject value;

        if (env.peek() == MangolangObjects.OBJECT_PREFIX) {
            value = env.readObject();
        } else {
            int sourceLow = env.next() & 0xFF;
            int sourceHigh = env.next() & 0xFF;
            int sourceIndex = (sourceHigh << 8) | sourceLow;
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

        output.add((byte) (index & 0xFF));
        output.add((byte) ((index >> 8) & 0xFF));

        MangolangObject literal = MangolangObjectCompiler.literalFromToken(args[2].toString());
        if (literal != null) {
            MangolangObjectCompiler.emitObject(output, literal);
        } else {
            int sourceIndex = ctx.getVariableIndex(args[2].toString());
            output.add((byte) (sourceIndex & 0xFF));
            output.add((byte) ((sourceIndex >> 8) & 0xFF));
        }
    }
}