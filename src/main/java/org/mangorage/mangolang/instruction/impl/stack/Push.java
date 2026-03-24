package org.mangorage.mangolang.instruction.impl.stack;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.register.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

@AutoRegisterInstruction
public final class Push implements Instruction {
    public void execute(VMEnvironment env) {
        MangolangObject obj = env.readObject();
        env.getStack().push(obj);
    }

    @Override
    public void emitBytecode(List<Byte> output, CompilerContext ctx, Object... args) {
        if (args.length != 1) {
            throw new RuntimeException("Push requires exactly 1 argument");
        }

        MangolangObject literal = MangolangObjects.literalFromToken(args[0].toString());
        if (literal == null) {
            throw new RuntimeException("Push argument must be a literal value: " + args[0]);
        }

        MangolangObjects.emitObject(output, literal);
    }
}
