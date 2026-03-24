package org.mangorage.mangolang.instruction.impl.output;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.register.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.object.MangolangObjectCompiler;
import org.mangorage.mangolang.object.MangolangObjects;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

@AutoRegisterInstruction
public final class Print implements Instruction {

    @Override
    public void execute(VMEnvironment env) {
        // Inline literals are encoded as serialized Mangolang objects directly after the opcode.
        int next = env.peek();
        if (next == MangolangObjects.OBJECT_PREFIX) {
            var obj = env.readObject();
            env.getTerminal().println(obj.toDisplayString());
            return;
        }

        // Otherwise, pop from the stack and print
        if (!env.getStack().isEmpty()) {
            var obj = env.getStack().pop();
            env.getTerminal().println(obj.toDisplayString());
        }
    }

    public void emitBytecode(List<Byte> output, CompilerContext ctx, Object... args) {
        if (args.length >= 1) {
            // Reconstruct raw argument (preserve spaces between tokens)
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                if (i > 0) sb.append(' ');
                sb.append(args[i].toString());
            }
            String raw = sb.toString();
            if (raw.length() >= 2 && raw.charAt(0) == '"' && raw.charAt(raw.length() - 1) == '"') {
                String str = raw.substring(1, raw.length() - 1);
                MangolangObjectCompiler.emitObject(output, new org.mangorage.mangolang.object.impl.StringMLObject(str));
            }
        }
    }
}