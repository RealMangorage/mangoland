package org.mangorage.mangolang.compiler.impl;

import org.mangorage.mangolang.compiler.BlockContext;
import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.compiler.CompilerEmitUtil;
import org.mangorage.mangolang.compiler.LexerNode;
import org.mangorage.mangolang.compiler.LexerOutput;
import org.mangorage.mangolang.instruction.InstructionSet;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;

public final class IfStatementLexerNode implements LexerNode {
    @Override
    public LexerOutput handle(String[] parts, String name, Stack<BlockContext> blocks, List<Byte> out, CompilerContext ctx, InstructionSet set) {
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
                CompilerEmitUtil.emitSimpleCondition(condStr, out, ctx, set);

                // Now emit the conditional jump placeholder to skip the then-body when false
                // Encoding: [opcode][trueAddrLo][trueAddrHi][falseAddrLo][falseAddrHi]
                BlockContext b = new BlockContext(BlockContext.Type.IF, out.size());
                blocks.push(b);
                b.setCondJumpAddress(out.size());
                out.add((byte) set.requireOpcode("jump_if_false"));
                out.add((byte) 0); // trueAddr low
                out.add((byte) 0); // trueAddr high
                out.add((byte) 0); // falseAddr low
                out.add((byte) 0); // falseAddr high
                // If the delimiter was 'then' and there are tokens after it on the same line,
                // we should continue processing the rest of this line as normal instructions.
                if (!("then".equals(delim) && delimIdx + 1 < parts.length)) {
                    return new LexerOutput(true);
                }
            }

            // Non-inline: push IF context and expect a separate 'then' token later
            // Only push when there was no inline delimiter parsed above.
            // (The inline branch already pushed a BlockContext when needed.)
            blocks.push(new BlockContext(BlockContext.Type.IF, out.size()));
            return new LexerOutput(true);
        }


        if (name.equals("else")) {
            BlockContext b = blocks.isEmpty() ? null : blocks.peek();
            if (b == null || b.getType() != BlockContext.Type.IF) {
                throw new RuntimeException("Unexpected 'else' without 'if'");
            }

            // We are at the boundary between then-body and else-body. We'll emit
            // an unconditional jump here (to skip the else body) which occupies
            // three slots (opcode + 2-byte placeholder). Therefore the actual start of
            // the else-body will be current out.size() + 3.
            int elseStart = out.size() + 3;

            // Patch the conditional jump to point to the start of the else-body
            int p = b.getCondJumpAddress();
            out.set(p + 1, (byte) (elseStart & 0xFF));
            out.set(p + 2, (byte) ((elseStart >> 8) & 0xFF));
            // Ensure false-target jumps into the then-body (immediately after the placeholders)
            int thenStart = p + 5; // opcode + 4 bytes of placeholders
            out.set(p + 3, (byte) (thenStart & 0xFF));
            out.set(p + 4, (byte) ((thenStart >> 8) & 0xFF));

            // Emit an unconditional jump to skip the else body after then-body
            out.add((byte) set.requireOpcode("jump"));
            out.add((byte) 0); // placeholder low
            out.add((byte) 0); // placeholder high
            b.setElseJumpAddress(out.size() - 2); // index of placeholder low byte
            return new LexerOutput(true);
        }

        // ===== THEN (marks end of condition, start of then-body) =====
        if (name.equals("then") || (name.equals("end") && !blocks.isEmpty() && blocks.peek().getType() == BlockContext.Type.IF)) {
            BlockContext b = blocks.isEmpty() ? null : blocks.peek();
            if (b == null || b.getType() != BlockContext.Type.IF) {
                throw new RuntimeException("Unexpected 'then' without 'if'");
            }
            // Emit conditional jump placeholder; if condition is false, skip the then body
            b.setCondJumpAddress(out.size());
            out.add((byte) set.requireOpcode("jump_if_false"));
            out.add((byte) 0);
            out.add((byte) 0);
            out.add((byte) 0);
            out.add((byte) 0);
            if (!name.equals("end")) {
                return new LexerOutput(true);
            }
        }

        return new LexerOutput(false);
    }
}
