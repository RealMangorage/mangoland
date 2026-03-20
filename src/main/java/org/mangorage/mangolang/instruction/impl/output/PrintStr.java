package org.mangorage.mangolang.instruction.impl.output;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VM;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.Arrays;
import java.util.List;

public final class PrintStr implements Instruction {

    @Override
    public void execute(VMEnvironment env) {
        // 1. Read the length of the string from the bytecode stream
        int length = env.next();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            // 2. Read each character code and append to the builder
            sb.append((char) env.next());
        }

        env.getTerminal().println(sb.toString());
    }

    @Override
    public int getArgCount() {
        /** * Change: In a fixed-length system, this is tricky.
         * However, for the compiler's sake, "1" usually refers to the
         * high-level argument (the String object).
         */
        return 1;
    }

    public void emitBytecode(List<Integer> output, CompilerContext ctx, Object... args) {
        if (args.length < 1) return;

        // 1. Reconstruct the full string from the split arguments
        String fullInput = String.join(" ", Arrays.stream(args)
                .map(Object::toString)
                .toArray(String[]::new));

        String text = fullInput;

        // 2. Extract content between the first and last quotes
        int firstQuote = text.indexOf('"');
        int lastQuote = text.lastIndexOf('"');

        if (firstQuote != -1 && lastQuote != -1 && firstQuote != lastQuote) {
            text = text.substring(firstQuote + 1, lastQuote);
        } else {
            // Fallback: If no quotes, just use the first arg (e.g. printstr MyVar)
            text = args[0].toString();
        }

        // 3. Handle Escapes
        text = unescapeString(text);

        // 4. Emit Length + Characters
        output.add(text.length());
        for (char c : text.toCharArray()) {
            output.add((int) c);
        }
    }

    private String unescapeString(String s) {
        return s.replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}