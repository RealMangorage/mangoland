package org.mangorage.mangolang.compiler.impl;

import org.mangorage.mangolang.compiler.BlockContext;
import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.compiler.LexerNode;
import org.mangorage.mangolang.compiler.LexerOutput;
import org.mangorage.mangolang.instruction.InstructionSet;

import java.util.List;
import java.util.Stack;

public final class IfStatementLexerNode implements LexerNode {
    @Override
    public LexerOutput handle(String[] parts, String name, Stack<BlockContext> blocks, List<Integer> out, CompilerContext ctx, InstructionSet set) {
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
