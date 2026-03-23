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
        int flag = env.next();
        if (flag == 1) {
            int len = env.next();
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) {
                sb.append((char) env.next());
            }
            env.getTerminal().println(sb.toString());
            return;
        }

        // Fallback: treat flag as length
        int len = flag;
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append((char) env.next());
        }
        env.getTerminal().println(sb.toString());
    }

    public void emitBytecode(List<Integer> output, CompilerContext ctx, Object... args) {
        if (args.length >= 1) {
            // If the argument looks like a quoted string, emit it inline as: [flag=1][len][chars...]
            String raw = args[0].toString();
            if (raw.length() >= 2 && raw.charAt(0) == '"' && raw.charAt(raw.length() - 1) == '"') {
                String str = raw.substring(1, raw.length() - 1);
                output.add(1); // string flag
                output.add(str.length());
                for (char c : str.toCharArray()) output.add((int) c);
            }
        }
    }
}