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



            // ===== IF START =====
            if (name.equals("if")) {
                // If the 'if' is inline and contains 'do' on the same line, try to parse a simple condition
                int delimIdx = -1;
                String delim = null;
                for (int i = 1; i < parts.length; i++) {
                    if (parts[i].equalsIgnoreCase("do") || parts[i].equalsIgnoreCase("then")) {
                        delimIdx = i;
                        delim = parts[i].toLowerCase();
                        break;
                    }
                }

                if (delimIdx != -1) {
                    // Join the condition tokens between 'if' and the delimiter (do/then)
                    String condStr = String.join(" ", Arrays.copyOfRange(parts, 1, delimIdx));

                    // Simple pattern: (var == value) allowing optional surrounding parens
                    Pattern p = Pattern.compile("\\(?\\s*([a-zA-Z_]\\w*)\\s*(==|!=)\\s*([0-9]+)\\s*\\)?");
                    Matcher m = p.matcher(condStr);
                    if (!m.matches()) {
                        throw new RuntimeException("Unsupported inline if condition: " + condStr);
                    }

                    String var = m.group(1);
                    String op = m.group(2);
                    String val = m.group(3);

                    // Emit the equivalent instructions for the simple condition
                    // load var
                    int opLoad = set.requireOpcode("load");
                    out.add(opLoad);
                    Instruction instLoad = set.get(opLoad);
                    instLoad.emitBytecode(out, ctx, new Object[]{var});

                    // push value
                    int opPush = set.requireOpcode("push");
                    out.add(opPush);
                    set.get(opPush).emitBytecode(out, ctx, new Object[]{val});

                    // equals (we only support '==' for now; '!=' handled by comparing result to 0 later)
                    int opEq = set.requireOpcode("equals");
                    out.add(opEq);
                    set.get(opEq).emitBytecode(out, ctx, new Object[]{});

                    if (op.equals("!=")) {
                        // Invert the boolean: equals produced 1 when equal; we want 1 when not equal.
                        // We'll emit: push 0 ; equals  -> compares (equalsResult == 0)
                        int opPush0 = set.requireOpcode("push");
                        out.add(opPush0);
                        set.get(opPush0).emitBytecode(out, ctx, new Object[]{"0"});

                        int opEq2 = set.requireOpcode("equals");
                        out.add(opEq2);
                        set.get(opEq2).emitBytecode(out, ctx, new Object[]{});
                    }

                    // Now emit the conditional jump placeholder to skip the then-body when false
                    BlockContext b = new BlockContext(BlockContext.Type.IF, out.size());
                    blocks.push(b);
                    b.condJumpAddress = out.size();
                    out.add(set.requireOpcode("jump_if_false"));
                    out.add(0); // true-target placeholder (patched to else/exit)
                    out.add(0); // false-target placeholder (points to instruction after these placeholders)
                    // If the delimiter was 'then' and there are tokens after it on the same line,
                    // we should continue processing the rest of this line as normal instructions.
                    if ("then".equals(delim) && delimIdx + 1 < parts.length) {
                        // Rebuild the remainder of the line and process it immediately
                        String[] remainder = Arrays.copyOfRange(parts, delimIdx + 1, parts.length);
                        // Create a pseudo-line and fall through to normal instruction handling by
                        // replacing 'parts' and 'name' for this iteration.
                        parts = remainder;
                        // fall through to emit this instruction below
                    } else {
                        continue;
                    }
                }

                // Non-inline: push IF context and expect a separate 'then' token later
                blocks.push(new BlockContext(BlockContext.Type.IF, out.size()));
                continue;
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