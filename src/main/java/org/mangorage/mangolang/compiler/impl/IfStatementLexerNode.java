package org.mangorage.mangolang.compiler.impl;

import org.mangorage.mangolang.compiler.BlockContext;
import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.compiler.LexerNode;
import org.mangorage.mangolang.compiler.LexerOutput;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.instruction.InstructionSet;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IfStatementLexerNode implements LexerNode {
    @Override
    public LexerOutput handle(String[] parts, String name, Stack<BlockContext> blocks, List<Integer> out, CompilerContext ctx, InstructionSet set) {
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
                if (!("then".equals(delim) && delimIdx + 1 < parts.length)) {
                    return new LexerOutput(null, true);
                }
            }

            // Non-inline: push IF context and expect a separate 'then' token later
            blocks.push(new BlockContext(BlockContext.Type.IF, out.size()));
            return new LexerOutput(null, true);
        }


        if (name.equals("else")) {
            BlockContext b = blocks.peek();
            if (b == null || b.type != BlockContext.Type.IF) {
                throw new RuntimeException("Unexpected 'else' without 'if'");
            }

            // We are at the boundary between then-body and else-body. We'll emit
            // an unconditional jump here (to skip the else body) which occupies
            // two slots (opcode + placeholder). Therefore the actual start of
            // the else-body will be current out.size() + 2.
            int elseStart = out.size() + 2;

            // Patch the conditional jump to point to the start of the else-body
            out.set(b.condJumpAddress + 1, elseStart);
            // Ensure false-target jumps into the then-body (immediately after the two placeholders)
            out.set(b.condJumpAddress + 2, b.condJumpAddress + 3);

            // Emit an unconditional jump to skip the else body after then-body
            out.add(set.requireOpcode("jump"));
            out.add(0); // placeholder to be patched at 'end'
            b.elseJumpAddress = out.size() - 1; // index of the placeholder value
            return new LexerOutput(null, true);
        }

        // ===== THEN (marks end of condition, start of then-body) =====
        if (name.equals("then") || (name.equals("end") && blocks.peek() != null && blocks.peek().type == BlockContext.Type.IF)) {
            BlockContext b = blocks.peek();
            if (b == null || b.type != BlockContext.Type.IF) {
                throw new RuntimeException("Unexpected 'then' without 'if'");
            }
            // Emit conditional jump placeholder; if condition is false, skip the then body
            b.condJumpAddress = out.size();
            out.add(set.requireOpcode("jump_if_false"));
            out.add(0); // true-target placeholder
            out.add(0); // false-target placeholder
            if (!name.equals("end")) {
                return new LexerOutput(null, true);
            }
        }

        return new LexerOutput(null, false);
    }
}
