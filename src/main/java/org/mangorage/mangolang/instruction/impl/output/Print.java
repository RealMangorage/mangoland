package org.mangorage.mangolang.instruction.impl.output;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;
import java.util.List;

@AutoRegisterInstruction
public final class Print implements Instruction {

    @Override
    public void execute(VMEnvironment env) {
        if (!env.getStack().isEmpty()) {
            var obj = env.getStack().pop();
            env.getTerminal().println("" + obj);
            return;
        }

        // No value on stack => expect an immediate string encoded in the bytecode
        // Read an encoded object from bytecode
        var obj = env.readObject();
        env.getTerminal().println("" + obj);
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
                new org.mangorage.mangolang.object.impl.StringMLObject(str).emitBytes(output);
            }
        }
    }
}