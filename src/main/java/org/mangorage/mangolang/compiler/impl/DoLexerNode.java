package org.mangorage.mangolang.compiler.impl;

import org.mangorage.mangolang.compiler.BlockContext;
import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.compiler.LexerNode;
import org.mangorage.mangolang.compiler.LexerOutput;
import org.mangorage.mangolang.instruction.InstructionSet;

import java.util.List;
import java.util.Stack;

public final class DoLexerNode implements LexerNode {
    @Override
    public LexerOutput handle(String[] parts, String name, Stack<BlockContext> blocks, List<Integer> out, CompilerContext ctx, InstructionSet set) {
        if (name.equals("do")) {
            BlockContext b = blocks.peek();
            if (b == null || b.getType() != BlockContext.Type.WHILE) {
                throw new RuntimeException("Unexpected 'do' without 'while'");
            }
            b.setCondJumpAddress(out.size());
            out.add(set.requireOpcode("jump_if_false"));
            out.add(0); // true-target placeholder (patched at 'end' to loop exit)
            out.add(0); // false-target placeholder (points to instruction after these two placeholders)
            return new LexerOutput(true);
        }
        return new LexerOutput(false);
    }
}
