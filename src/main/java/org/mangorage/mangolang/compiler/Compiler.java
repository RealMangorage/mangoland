package org.mangorage.mangolang.compiler;

import org.mangorage.mangolang.compiler.impl.BreakLexerNode;
import org.mangorage.mangolang.compiler.impl.DoLexerNode;
import org.mangorage.mangolang.compiler.impl.EndLexerNode;
import org.mangorage.mangolang.compiler.impl.FunctionLexerNode;
import org.mangorage.mangolang.compiler.impl.IfStatementLexerNode;
import org.mangorage.mangolang.compiler.impl.WhileLexerNode;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.instruction.InstructionSet;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Compiler {
    private final InstructionSet set;

    public Compiler(InstructionSet set) {
        this.set = set;
    }

    public int[] compile(String source) {
        List<LexerNode> nodes = List.of(
                new BreakLexerNode(),
                new DoLexerNode(),
                new EndLexerNode(),
                new FunctionLexerNode(),
                new IfStatementLexerNode(),
                new WhileLexerNode()
        );

        CompilerContext ctx = new CompilerContext();
        List<Integer> out = new ArrayList<>();


        // Upgrade from `boolean inFunction` to a stack to support nesting!
        Stack<BlockContext> blocks = new Stack<>();

        String[] lines = source.split("\\n");

        main: for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            int commentIndex = line.indexOf('#');
            if (commentIndex != -1)
                line = line.substring(0, commentIndex).trim();

            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            String name = parts[0].toLowerCase();


            for (LexerNode node : nodes) {
                final var output = node.handle(
                        parts,
                        name,
                        blocks,
                        out,
                        ctx,
                        set
                );

                if (output.doContinue())
                    continue main;
            }

            // ===== NORMAL INSTRUCTION =====
            if (System.getProperty("mangolang.debug") != null) {
                System.out.println("[Compiler] line='" + line + "' name='" + name + "' args='" + Arrays.toString(Arrays.copyOfRange(parts, 1, parts.length)) + "'");
            }
            int opcode = set.requireOpcode(name);
            out.add(opcode);

            Instruction inst = set.get(opcode);
            Object[] args = Arrays.copyOfRange(parts, 1, parts.length);
            inst.emitBytecode(out, ctx, args);
        }

        if (!blocks.isEmpty()) throw new RuntimeException("Missing 'end' for block");

        out.add(set.requireOpcode("halt"));

        // ===== SANITY CHECK =====
        for (int i = 0; i < out.size(); i++) {
            if (out.get(i) == null) {
                throw new RuntimeException("Null bytecode at index " + i);
            }
        }

        // Optional debug: dump raw bytecode when system property is set
        if (System.getProperty("mangolang.dumpbytecode") != null) {
            System.out.println("[Compiler] raw bytecode: " + out);
        }

        return out.stream().mapToInt(i -> i).toArray();
    }
}