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
            // Search down the stack to find the nearest loop (allows breaking out of a loop inside an if/function)
            BlockContext loop = null;
            for (int i = blocks.size() - 1; i >= 0; i--) {
                if (blocks.get(i).getType() == BlockContext.Type.WHILE) {
                    loop = blocks.get(i);
                    break;
                }
            }
            if (loop == null) throw new RuntimeException("Cannot 'break' outside of a loop");

            loop.addBreak(out.size());
            out.add((byte) set.requireOpcode("jump"));
            out.add((byte) 0); // placeholder, patched at 'end'
            return new LexerOutput(true);
        }
        return new LexerOutput(false);
    }
}
