package org.mangorage.mangolang.compiler.impl;

import org.mangorage.mangolang.compiler.BlockContext;
import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.compiler.LexerNode;
import org.mangorage.mangolang.compiler.LexerOutput;
import org.mangorage.mangolang.instruction.InstructionSet;

import java.util.List;
import java.util.Stack;

public final class BreakLexerNode implements LexerNode {
    @Override
    public LexerOutput handle(String[] parts, String name, Stack<BlockContext> blocks, List<Byte> out, CompilerContext ctx, InstructionSet set) {
        if (name.equals("break")) {
            if (parts.length > 2) {
                throw new RuntimeException("Break accepts at most one optional label");
            }

            String requestedLabel = parts.length == 2 ? parts[1] : null;
            BlockContext loop = null;

            for (int i = blocks.size() - 1; i >= 0; i--) {
                BlockContext candidate = blocks.get(i);
                if (candidate.getType() != BlockContext.Type.WHILE) {
                    continue;
                }

                if (requestedLabel == null) {
                    loop = candidate;
                    break;
                }

                if (requestedLabel.equals(candidate.getLabel())) {
                    loop = candidate;
                    break;
                }
            }

            if (loop == null) {
                if (requestedLabel == null) {
                    throw new RuntimeException("Cannot 'break' outside of a loop");
                }
                throw new RuntimeException("Unknown loop label: " + requestedLabel);
            }

            loop.addBreak(out.size());
            out.add((byte) set.requireOpcode("jump"));
            out.add((byte) 0); // placeholder low
            out.add((byte) 0); // placeholder high
            return new LexerOutput(true);
        }
        return new LexerOutput(false);
    }
}
