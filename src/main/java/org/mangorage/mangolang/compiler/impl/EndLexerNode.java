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
    public LexerOutput handle(String[] parts, String name, Stack<BlockContext> blocks, List<Byte> out, CompilerContext ctx, InstructionSet set) {
        // ===== END =====
        if (name.equals("end")) {
            if (blocks.isEmpty()) throw new RuntimeException("Unexpected 'end'");
            BlockContext b = blocks.pop();

            if (b.getType() == BlockContext.Type.FUNCTION) {
                out.add((byte) set.requireOpcode("return"));
                // Patch the jump so the VM skips over the function definition
                int target = out.size();
                int placeholderIndex = b.getStartAddress() + 1; // low byte position
                out.set(placeholderIndex, (byte) (target & 0xFF));
                out.set(placeholderIndex + 1, (byte) ((target >> 8) & 0xFF));
            }
            else if (b.getType() == BlockContext.Type.WHILE) {
                // Unconditional jump back to the 'while' condition
                System.out.println("[End] WHILE end: startAddr=" + b.getStartAddress() + " out.size(before)=" + out.size());
                out.add((byte) set.requireOpcode("jump"));
                int target = b.getStartAddress();
                out.add((byte) (target & 0xFF));
                out.add((byte) ((target >> 8) & 0xFF));

                int loopExitAddress = out.size();

                // 1. Patch the 'do' conditional jump
                // condJumpAddress points at opcode; +1 is true-target placeholder, +2 is false-target
                int la = loopExitAddress;
                int p = b.getCondJumpAddress(); // opcode index
                out.set(p + 1, (byte) (la & 0xFF));
                out.set(p + 2, (byte) ((la >> 8) & 0xFF));
                out.set(p + 3, (byte) (la & 0xFF));
                out.set(p + 4, (byte) ((la >> 8) & 0xFF));

                // 2. Patch all 'break' statements inside this loop
                for (int breakAddr : b.getBreaks()) {
                    int idx = breakAddr;
                    out.set(idx + 1, (byte) (loopExitAddress & 0xFF));
                    out.set(idx + 2, (byte) ((loopExitAddress >> 8) & 0xFF));
                }
            }
            else if (b.getType() == BlockContext.Type.IF) {
                // If there was an ELSE branch, patch its unconditional jump placeholder
                if (b.getElseJumpAddress() != -1) {
                    int idx = b.getElseJumpAddress();
                    int target = out.size();
                    out.set(idx, (byte) (target & 0xFF));
                    out.set(idx + 1, (byte) ((target >> 8) & 0xFF));
                    // Also patch the original conditional's false-target to point to the start of the then-body
                    int p = b.getCondJumpAddress();
                    out.set(p + 2, (byte) ((p + 3) & 0xFF));
                    out.set(p + 3, (byte) (((p + 3) >> 8) & 0xFF));
                } else {
                    // No ELSE: patch the conditional jump so that
                    // - the first placeholder (trueAddr) points to the instruction after the then-body (skip)
                    // - the second placeholder (falseAddr) points to the start of the then-body (immediately after the placeholders)
                    int p = b.getCondJumpAddress();
                    int trueAddr = out.size();
                    int falseAddr = p + 3;
                    out.set(p + 1, (byte) (trueAddr & 0xFF));
                    out.set(p + 2, (byte) ((trueAddr >> 8) & 0xFF));
                    out.set(p + 3, (byte) (falseAddr & 0xFF));
                    out.set(p + 4, (byte) ((falseAddr >> 8) & 0xFF));
                }
            }
            return new LexerOutput(true);
        }
        return new LexerOutput(false);
    }
}
