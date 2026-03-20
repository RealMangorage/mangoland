package org.mangorage.mangolang.compiler.impl;

import org.mangorage.mangolang.compiler.BlockContext;
import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.compiler.LexerNode;
import org.mangorage.mangolang.compiler.LexerOutput;
import org.mangorage.mangolang.instruction.InstructionSet;

import java.util.List;
import java.util.Stack;

public final class EndLexerNode implements LexerNode {
    @Override
    public LexerOutput handle(String[] parts, String name, Stack<BlockContext> blocks, List<Integer> out, CompilerContext ctx, InstructionSet set) {
        // ===== END =====
        if (name.equals("end")) {
            if (blocks.isEmpty()) throw new RuntimeException("Unexpected 'end'");
            BlockContext b = blocks.pop();

            if (b.type == BlockContext.Type.FUNCTION) {
                out.add(set.requireOpcode("return"));
                // Patch the jump so the VM skips over the function definition
                out.set(b.startAddress + 1, out.size());
            }
            else if (b.type == BlockContext.Type.WHILE) {
                // Unconditional jump back to the 'while' condition
                out.add(set.requireOpcode("jump"));
                out.add(b.startAddress);

                int loopExitAddress = out.size();

                // 1. Patch the 'do' conditional jump
                // condJumpAddress points at opcode; +1 is true-target placeholder, +2 is false-target
                out.set(b.condJumpAddress + 1, loopExitAddress);
                out.set(b.condJumpAddress + 2, loopExitAddress);

                // 2. Patch all 'break' statements inside this loop
                for (int breakAddr : b.breaks) {
                    out.set(breakAddr + 1, loopExitAddress);
                }
            }
            else if (b.type == BlockContext.Type.IF) {
                // If there was an ELSE branch, patch its unconditional jump placeholder
                if (b.elseJumpAddress != -1) {
                    out.set(b.elseJumpAddress, out.size());
                    // Also patch the original conditional's false-target to point to the start of the then-body
                    out.set(b.condJumpAddress + 2, b.condJumpAddress + 3);
                } else {
                    // No ELSE: patch the conditional jump to skip the then-body
                    out.set(b.condJumpAddress + 1, out.size());
                    out.set(b.condJumpAddress + 2, out.size());
                }
            }
            return new LexerOutput(true);
        }
        return new LexerOutput(false);
    }
}
